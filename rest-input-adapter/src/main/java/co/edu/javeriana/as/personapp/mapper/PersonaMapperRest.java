package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification() == null ? null : person.getIdentification() + "",
				person.getFirstName(),
				person.getLastName(),
				person.getAge() == null ? null : person.getAge() + "",
				person.getGender() == null ? null : person.getGender().toString(),
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) throws InvalidOptionException {
		RequestParser.requireBody(request);
		Person person = new Person();
		person.setIdentification(RequestParser.requiredInteger("dni", request.getDni()));
		person.setFirstName(RequestParser.requiredText("firstName", request.getFirstName()));
		person.setLastName(RequestParser.requiredText("lastName", request.getLastName()));
		person.setGender(parseGender(request.getSex()));
		person.setAge(RequestParser.optionalNonNegativeInteger("age", request.getAge()));
		return person;
	}

	/** Acepta MALE, FEMALE, OTHER y las abreviaturas M y F, sin importar mayusculas. */
	private Gender parseGender(String sex) throws InvalidOptionException {
		String value = RequestParser.requiredText("sex", sex).toUpperCase();
		switch (value) {
		case "M":
		case "MALE":
			return Gender.MALE;
		case "F":
		case "FEMALE":
			return Gender.FEMALE;
		case "OTHER":
			return Gender.OTHER;
		default:
			throw new InvalidOptionException("The field 'sex' must be MALE, FEMALE, OTHER, M or F: " + sex);
		}
	}
		
}
