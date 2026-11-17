package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfessionMapperRest;
import co.edu.javeriana.as.personapp.mapper.RequestParser;
import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfessionResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST de Profesion. No guarda estado: el caso de uso se arma
 * en cada peticion segun la base pedida.
 */
@Slf4j
@Adapter
public class ProfessionInputAdapterRest {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfessionMapperRest professionMapperRest;

	/** Arma el caso de uso con el OutputPort de la base elegida (Maria o Mongo). */
	private ProfessionInputPort professionInputPort(DatabaseOption database) {
		return new ProfessionUseCase(
				database == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
	}

	/** Lista todo en la base elegida y lo convierte a respuesta REST. */
	public List<ProfessionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Profession in Input Adapter");
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return professionInputPort(option).findAll().stream()
				.map(profession -> professionMapperRest.fromDomainToAdapterRest(profession, label))
				.collect(Collectors.toList());
	}

	/** Valida el request, lo pasa a dominio y lo guarda via caso de uso (POST). */
	public ProfessionResponse crearProfesion(String database, ProfessionRequest request)
			throws InvalidOptionException {
		DatabaseOption option = DatabaseResolver.parse(database);
		Profession profession = professionMapperRest.fromAdapterToDomain(request);
		Profession created = professionInputPort(option).create(profession);
		return professionMapperRest.fromDomainToAdapterRest(created, DatabaseResolver.label(option));
	}

	/** Valida y reemplaza el registro existente; NoExist si no esta (PUT). */
	public ProfessionResponse editarProfesion(String database, Integer id, ProfessionRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		RequestParser.requireBody(request);
		request.setId(String.valueOf(id));
		Profession edited = professionInputPort(option).edit(id, professionMapperRest.fromAdapterToDomain(request));
		return professionMapperRest.fromDomainToAdapterRest(edited, DatabaseResolver.label(option));
	}

	/** Borra el registro; NoExist si no existe (DELETE). */
	public Boolean eliminarProfesion(String database, Integer id) throws InvalidOptionException, NoExistException {
		return professionInputPort(DatabaseResolver.parse(database)).drop(id);
	}

	/** Busca por id/llave; NoExist si no esta (GET por id). */
	public ProfessionResponse buscarProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		return professionMapperRest.fromDomainToAdapterRest(professionInputPort(option).findOne(id),
				DatabaseResolver.label(option));
	}

	/** Cuenta los registros de la base elegida (GET /count). */
	public Integer contarProfesiones(String database) throws InvalidOptionException {
		return professionInputPort(DatabaseResolver.parse(database)).count();
	}

}
