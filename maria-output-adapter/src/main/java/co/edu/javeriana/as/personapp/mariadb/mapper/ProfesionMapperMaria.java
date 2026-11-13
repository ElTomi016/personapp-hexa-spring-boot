package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class ProfesionMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	public ProfesionEntity fromDomainToAdapter(Profession profession) {
		Objects.requireNonNull(profession, "profession is required");
		ProfesionEntity profesionEntity = new ProfesionEntity();
		profesionEntity.setId(profession.getIdentification());
		profesionEntity.setNom(profession.getName());
		profesionEntity.setDes(validateDes(profession.getDescription()));
		profesionEntity.setEstudios(validateEstudios(profession.getStudies(), profesionEntity));
		return profesionEntity;
	}

	private String validateDes(String description) {
		return description != null ? description : "";
	}

	private List<EstudiosEntity> validateEstudios(List<Study> studies, ProfesionEntity owner) {
		if (studies == null || studies.isEmpty()) {
			return new ArrayList<EstudiosEntity>();
		}
		return studies.stream().map(study -> {
			EstudiosEntity estudio = estudiosMapperMaria.fromDomainToAdapter(study);
			estudio.setProfesion(owner);
			return estudio;
		}).collect(Collectors.toList());
	}

	/** Conversion completa: incluye los estudios (con persona y profesion superficiales). */
	public Profession fromAdapterToDomain(ProfesionEntity profesionEntity) {
		Profession profession = fromAdapterToDomainShallow(profesionEntity);
		profession.setStudies(validateStudies(profesionEntity.getEstudios()));
		return profession;
	}

	/** Solo datos propios de la profesion, sin lista de estudios. */
	public Profession fromAdapterToDomainShallow(ProfesionEntity profesionEntity) {
		Profession profession = new Profession();
		profession.setIdentification(profesionEntity.getId());
		profession.setName(profesionEntity.getNom());
		profession.setDescription(validateDescription(profesionEntity.getDes()));
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}

	private String validateDescription(String des) {
		return des != null ? des : "";
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}
