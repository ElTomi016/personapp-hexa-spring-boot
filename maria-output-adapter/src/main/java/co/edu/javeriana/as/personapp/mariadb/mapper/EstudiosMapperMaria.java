package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

/**
 * [Integrante A] Traduce Study (dominio) <-> EstudiosEntity (MariaDB).
 * BUG ORIGINAL: fromDomainToAdapter devolvia null y el mapeo a dominio
 * seguia persona/profesion completas, provocando StackOverflowError (ciclo
 * Persona -> Estudios -> Persona). Ahora usa conversiones "shallow".
 */
@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
		// [A] Validaciones explicitas (antes habia @NonNull sin efecto).
		Objects.requireNonNull(study, "study is required");
		Objects.requireNonNull(study.getPerson(), "the study person is required");
		Objects.requireNonNull(study.getProfession(), "the study profession is required");
		Integer ccPer = Objects.requireNonNull(study.getPerson().getIdentification(), "person identification is required");
		Integer idProf = Objects.requireNonNull(study.getProfession().getIdentification(),
				"profession identification is required");
		// Llave compuesta (idProf, ccPer) de la tabla estudios.
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(ccPer);
		estudioPK.setIdProf(idProf);
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		// Referencias solo por id: las columnas son insertable=false, pero el objeto
		// devuelto por save() necesita persona y profesion para mapear de vuelta.
		estudio.setPersona(new PersonaEntity(ccPer));
		estudio.setProfesion(new ProfesionEntity(idProf));
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio; // BUG ORIGINAL: aqui se hacia "return null".
	}

	private Date validateFecha(LocalDate graduationDate) {
		return graduationDate != null
				? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
				: null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	/** Entidad -> dominio. Persona y profesion llegan sin listas para no ciclar. */
	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosEntity));
		study.setProfession(validateProfession(estudiosEntity));
		study.setGraduationDate(validateGraduationDate(estudiosEntity.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study;
	}

	private Person validatePerson(EstudiosEntity estudiosEntity) {
		// [A] Shallow: corta el ciclo Persona <-> Estudios (era la causa del StackOverflowError).
		if (estudiosEntity.getPersona() != null) {
			return personaMapperMaria.fromAdapterToDomainShallow(estudiosEntity.getPersona());
		}
		Person person = new Person();
		person.setIdentification(estudiosEntity.getEstudiosPK().getCcPer());
		return person;
	}

	private Profession validateProfession(EstudiosEntity estudiosEntity) {
		if (estudiosEntity.getProfesion() != null) {
			return profesionMapperMaria.fromAdapterToDomainShallow(estudiosEntity.getProfesion());
		}
		Profession profession = new Profession();
		profession.setIdentification(estudiosEntity.getEstudiosPK().getIdProf());
		return profession;
	}

	private LocalDate validateGraduationDate(Date fecha) {
		// java.sql.Date evita el error de toInstant() en fechas que vienen de JDBC.
		return fecha != null ? new java.sql.Date(fecha.getTime()).toLocalDate() : null;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
