package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada CLI de Telefono. No guarda estado: el caso de uso se arma en
 * cada llamada segun la base elegida en el menu.
 */
@Slf4j
@Adapter
public class PhoneInputAdapterCli {

	@Autowired
	@Qualifier("phoneOutputAdapterMaria")
	private PhoneOutputPort phoneOutputPortMaria;

	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phoneOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private TelefonoMapperCli telefonoMapperCli;

	private PhoneInputPort phoneInputPort(DatabaseOption database) {
		return database == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	public List<TelefonoModelCli> historial(String dbOption) throws InvalidOptionException {
		log.info("Into historial Phone in Input Adapter");
		return phoneInputPort(DatabaseResolver.parse(dbOption)).findAll().stream()
				.map(telefonoMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public TelefonoModelCli crear(String dbOption, TelefonoModelCli model)
			throws InvalidOptionException, NoExistException {
		Phone created = phoneInputPort(DatabaseResolver.parse(dbOption))
				.create(telefonoMapperCli.fromAdapterCliToDomain(model));
		return telefonoMapperCli.fromDomainToAdapterCli(created);
	}

	public TelefonoModelCli editar(String dbOption, String numero, TelefonoModelCli model)
			throws InvalidOptionException, NoExistException {
		model.setNumero(numero);
		Phone edited = phoneInputPort(DatabaseResolver.parse(dbOption)).edit(numero,
				telefonoMapperCli.fromAdapterCliToDomain(model));
		return telefonoMapperCli.fromDomainToAdapterCli(edited);
	}

	public Boolean eliminar(String dbOption, String numero) throws InvalidOptionException, NoExistException {
		return phoneInputPort(DatabaseResolver.parse(dbOption)).drop(numero);
	}

	public TelefonoModelCli buscar(String dbOption, String numero) throws InvalidOptionException, NoExistException {
		return telefonoMapperCli
				.fromDomainToAdapterCli(phoneInputPort(DatabaseResolver.parse(dbOption)).findOne(numero));
	}

	public Integer contar(String dbOption) throws InvalidOptionException {
		return phoneInputPort(DatabaseResolver.parse(dbOption)).count();
	}

}
