package co.edu.javeriana.as.personapp.mongo.mapper;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

@Mapper
public class EstudiosMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	public EstudiosDocument fromDomainToAdapter(Study study) {
		Objects.requireNonNull(study, "study is required");
		Objects.requireNonNull(study.getPerson(), "the study person is required");
		Objects.requireNonNull(study.getProfession(), "the study profession is required");
		Integer ccPer = Objects.requireNonNull(study.getPerson().getIdentification(), "person identification is required");
		Integer idProf = Objects.requireNonNull(study.getProfession().getIdentification(),
				"profession identification is required");
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(buildId(ccPer, idProf));
		PersonaDocument persona = new PersonaDocument();
		persona.setId(ccPer);
		estudio.setPrimaryPersona(persona);
		ProfesionDocument profesion = new ProfesionDocument();
		profesion.setId(idProf);
		estudio.setPrimaryProfesion(profesion);
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	/** Id compuesto como texto: "cc-idProfesion". */
	public static String buildId(Integer identificationPerson, Integer identificationProfession) {
		return identificationPerson + "-" + identificationProfession;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(validatePerson(estudiosDocument.getPrimaryPersona()));
		study.setProfession(validateProfession(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(validateGraduationDate(estudiosDocument.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		return study;
	}

	private Person validatePerson(PersonaDocument persona) {
		if (persona == null) {
			throw new IllegalStateException("The study has no person");
		}
		return personaMapperMongo.fromAdapterToDomainShallow(persona);
	}

	private Profession validateProfession(ProfesionDocument profesion) {
		if (profesion == null) {
			throw new IllegalStateException("The study has no profession");
		}
		return profesionMapperMongo.fromAdapterToDomainShallow(profesion);
	}

	private LocalDate validateGraduationDate(LocalDate fecha) {
		return fecha;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
