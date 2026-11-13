package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.PhoneMapperRest;
import co.edu.javeriana.as.personapp.mapper.RequestParser;
import co.edu.javeriana.as.personapp.mapper.StudyMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST de Persona. No guarda estado: el caso de uso se arma
 * en cada peticion segun la base pedida.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	@Autowired
	private PhoneMapperRest phoneMapperRest;

	@Autowired
	private StudyMapperRest studyMapperRest;

	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	/** MariaDB solo guarda genero M o F; se rechaza OTHER para no terminar en un error interno. */
	private void validateGender(DatabaseOption database, Person person) throws InvalidOptionException {
		if (database == DatabaseOption.MARIA && person.getGender() == Gender.OTHER) {
			throw new InvalidOptionException("MariaDB only supports MALE or FEMALE as sex");
		}
	}

	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return personInputPort(option).findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, label)).collect(Collectors.toList());
	}

	public PersonaResponse crearPersona(String database, PersonaRequest request) throws InvalidOptionException {
		DatabaseOption option = DatabaseResolver.parse(database);
		Person person = personaMapperRest.fromAdapterToDomain(request);
		validateGender(option, person);
		Person created = personInputPort(option).create(person);
		return personaMapperRest.fromDomainToAdapterRest(created, DatabaseResolver.label(option));
	}

	public PersonaResponse editarPersona(String database, Integer cc, PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		RequestParser.requireBody(request);
		request.setDni(String.valueOf(cc));
		Person person = personaMapperRest.fromAdapterToDomain(request);
		validateGender(option, person);
		Person edited = personInputPort(option).edit(cc, person);
		return personaMapperRest.fromDomainToAdapterRest(edited, DatabaseResolver.label(option));
	}

	public Boolean eliminarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		return personInputPort(DatabaseResolver.parse(database)).drop(cc);
	}

	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		return personaMapperRest.fromDomainToAdapterRest(personInputPort(option).findOne(cc),
				DatabaseResolver.label(option));
	}

	public Integer contarPersonas(String database) throws InvalidOptionException {
		return personInputPort(DatabaseResolver.parse(database)).count();
	}

	public List<PhoneResponse> telefonosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return personInputPort(option).getPhones(cc).stream()
				.map(phone -> phoneMapperRest.fromDomainToAdapterRest(phone, label)).collect(Collectors.toList());
	}

	public List<StudyResponse> estudiosDePersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return personInputPort(option).getStudies(cc).stream()
				.map(study -> studyMapperRest.fromDomainToAdapterRest(study, label)).collect(Collectors.toList());
	}

}
