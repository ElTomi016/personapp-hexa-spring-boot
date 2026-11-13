package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada CLI de Persona. No guarda estado: el caso de uso se arma en
 * cada llamada segun la base elegida en el menu.
 */
@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	@Autowired
	private TelefonoMapperCli telefonoMapperCli;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	/** MariaDB solo guarda genero M o F; se rechaza OTHER para no terminar en un error interno. */
	private void validateGender(DatabaseOption database, PersonaModelCli model) throws InvalidOptionException {
		if (database == DatabaseOption.MARIA && "OTHER".equals(model.getGenero())) {
			throw new InvalidOptionException("MariaDB solo admite genero MALE o FEMALE");
		}
	}

	public List<PersonaModelCli> historial(String dbOption) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		return personInputPort(DatabaseResolver.parse(dbOption)).findAll().stream()
				.map(personaMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public PersonaModelCli crear(String dbOption, PersonaModelCli model) throws InvalidOptionException {
		DatabaseOption database = DatabaseResolver.parse(dbOption);
		validateGender(database, model);
		Person created = personInputPort(database).create(personaMapperCli.fromAdapterCliToDomain(model));
		return personaMapperCli.fromDomainToAdapterCli(created);
	}

	public PersonaModelCli editar(String dbOption, Integer cc, PersonaModelCli model)
			throws InvalidOptionException, NoExistException {
		DatabaseOption database = DatabaseResolver.parse(dbOption);
		validateGender(database, model);
		model.setCc(cc);
		Person edited = personInputPort(database).edit(cc, personaMapperCli.fromAdapterCliToDomain(model));
		return personaMapperCli.fromDomainToAdapterCli(edited);
	}

	public Boolean eliminar(String dbOption, Integer cc) throws InvalidOptionException, NoExistException {
		return personInputPort(DatabaseResolver.parse(dbOption)).drop(cc);
	}

	public PersonaModelCli buscar(String dbOption, Integer cc) throws InvalidOptionException, NoExistException {
		return personaMapperCli.fromDomainToAdapterCli(personInputPort(DatabaseResolver.parse(dbOption)).findOne(cc));
	}

	public Integer contar(String dbOption) throws InvalidOptionException {
		return personInputPort(DatabaseResolver.parse(dbOption)).count();
	}

	public List<TelefonoModelCli> telefonos(String dbOption, Integer cc)
			throws InvalidOptionException, NoExistException {
		return personInputPort(DatabaseResolver.parse(dbOption)).getPhones(cc).stream()
				.map(telefonoMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public List<EstudioModelCli> estudios(String dbOption, Integer cc)
			throws InvalidOptionException, NoExistException {
		return personInputPort(DatabaseResolver.parse(dbOption)).getStudies(cc).stream()
				.map(estudioMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

}
