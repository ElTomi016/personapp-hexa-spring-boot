package co.edu.javeriana.as.personapp.adapter;

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
import co.edu.javeriana.as.personapp.mapper.PhoneMapperRest;
import co.edu.javeriana.as.personapp.mapper.RequestParser;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST de Telefono. No guarda estado: el caso de uso se arma
 * en cada peticion segun la base pedida.
 */
@Slf4j
@Adapter
public class PhoneInputAdapterRest {

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
	private PhoneMapperRest phoneMapperRest;

	private PhoneInputPort phoneInputPort(DatabaseOption database) {
		return database == DatabaseOption.MARIA ? new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria)
				: new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
	}

	public List<PhoneResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Phone in Input Adapter");
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return phoneInputPort(option).findAll().stream()
				.map(phone -> phoneMapperRest.fromDomainToAdapterRest(phone, label)).collect(Collectors.toList());
	}

	public PhoneResponse crearTelefono(String database, PhoneRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		Phone created = phoneInputPort(option).create(phoneMapperRest.fromAdapterToDomain(request));
		return phoneMapperRest.fromDomainToAdapterRest(created, DatabaseResolver.label(option));
	}

	public PhoneResponse editarTelefono(String database, String number, PhoneRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		RequestParser.requireBody(request);
		request.setNumber(number);
		Phone edited = phoneInputPort(option).edit(number, phoneMapperRest.fromAdapterToDomain(request));
		return phoneMapperRest.fromDomainToAdapterRest(edited, DatabaseResolver.label(option));
	}

	public Boolean eliminarTelefono(String database, String number) throws InvalidOptionException, NoExistException {
		return phoneInputPort(DatabaseResolver.parse(database)).drop(number);
	}

	public PhoneResponse buscarTelefono(String database, String number)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		return phoneMapperRest.fromDomainToAdapterRest(phoneInputPort(option).findOne(number),
				DatabaseResolver.label(option));
	}

	public Integer contarTelefonos(String database) throws InvalidOptionException {
		return phoneInputPort(DatabaseResolver.parse(database)).count();
	}

}
