package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

/** [Integrante A] Profession (dominio) <-> ProfesionDocument (MongoDB); hijos en shallow. */
@Mapper
public class ProfesionMapperMongo {

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	/** Dominio -> documento de profesion. */
	public ProfesionDocument fromDomainToAdapter(Profession profession) {
		Objects.requireNonNull(profession, "profession is required");
		ProfesionDocument profesionDocument = new ProfesionDocument();
		profesionDocument.setId(profession.getIdentification());
		profesionDocument.setNom(profession.getName());
		profesionDocument.setDes(validateDes(profession.getDescription()));
		profesionDocument.setEstudios(validateEstudios(profession.getStudies()));
		return profesionDocument;
	}

	/** Descripcion nunca nula. */
	private String validateDes(String description) {
		return description != null ? description : "";
	}

	/** Estudios del dominio -> documentos. */
	private List<EstudiosDocument> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty() ? studies.stream()
				.map(study -> estudiosMapperMongo.fromDomainToAdapter(study)).collect(Collectors.toList())
				: new ArrayList<EstudiosDocument>();
	}

	/** Conversion completa: incluye los estudios (con persona y profesion superficiales). */
	public Profession fromAdapterToDomain(ProfesionDocument profesionDocument) {
		Profession profession = fromAdapterToDomainShallow(profesionDocument);
		profession.setStudies(validateStudies(profesionDocument.getEstudios()));
		return profession;
	}

	/** Solo datos propios de la profesion, sin lista de estudios. */
	public Profession fromAdapterToDomainShallow(ProfesionDocument profesionDocument) {
		Profession profession = new Profession();
		profession.setIdentification(profesionDocument.getId());
		profession.setName(profesionDocument.getNom());
		profession.setDescription(validateDescription(profesionDocument.getDes()));
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}

	/** Descripcion nunca nula. */
	private String validateDescription(String des) {
		return des != null ? des : "";
	}

	/** Documentos de estudios -> dominio. */
	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocument) {
		return estudiosDocument != null && !estudiosDocument.isEmpty() ? estudiosDocument.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}
}
