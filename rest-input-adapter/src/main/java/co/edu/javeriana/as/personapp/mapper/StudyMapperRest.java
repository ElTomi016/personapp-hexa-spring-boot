package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;

@Mapper
public class StudyMapperRest {

	public StudyResponse fromDomainToAdapterRest(Study study, String database) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new StudyResponse(
				person == null || person.getIdentification() == null ? null : person.getIdentification() + "",
				profession == null || profession.getIdentification() == null ? null
						: profession.getIdentification() + "",
				study.getGraduationDate() == null ? null : study.getGraduationDate().toString(),
				study.getUniversityName(),
				database,
				"OK");
	}

	/** Persona y profesion se arman solo con su identificacion; el adaptador de salida las resuelve por id. */
	public Study fromAdapterToDomain(StudyRequest request) throws InvalidOptionException {
		RequestParser.requireBody(request);
		Person person = new Person();
		person.setIdentification(RequestParser.requiredInteger("dni", request.getDni()));
		Profession profession = new Profession();
		profession.setIdentification(RequestParser.requiredInteger("professionId", request.getProfessionId()));
		Study study = new Study(person, profession);
		study.setGraduationDate(RequestParser.optionalDate("graduationDate", request.getGraduationDate()));
		study.setUniversityName(RequestParser.optionalText(request.getUniversityName()));
		return study;
	}
}
