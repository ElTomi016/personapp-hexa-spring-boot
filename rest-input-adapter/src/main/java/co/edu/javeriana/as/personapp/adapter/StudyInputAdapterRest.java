package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.RequestParser;
import co.edu.javeriana.as.personapp.mapper.StudyMapperRest;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada REST de Estudios (clave compuesta persona + profesion).
 * No guarda estado: el caso de uso se arma en cada peticion segun la base pedida.
 */
@Slf4j
@Adapter
public class StudyInputAdapterRest {

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	private StudyMapperRest studyMapperRest;

	private StudyInputPort studyInputPort(DatabaseOption database) {
		return new StudyUseCase(database == DatabaseOption.MARIA ? studyOutputPortMaria : studyOutputPortMongo);
	}

	public List<StudyResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial Study in Input Adapter");
		DatabaseOption option = DatabaseResolver.parse(database);
		String label = DatabaseResolver.label(option);
		return studyInputPort(option).findAll().stream()
				.map(study -> studyMapperRest.fromDomainToAdapterRest(study, label)).collect(Collectors.toList());
	}

	public StudyResponse crearEstudio(String database, StudyRequest request) throws InvalidOptionException {
		DatabaseOption option = DatabaseResolver.parse(database);
		Study created = studyInputPort(option).create(studyMapperRest.fromAdapterToDomain(request));
		return studyMapperRest.fromDomainToAdapterRest(created, DatabaseResolver.label(option));
	}

	public StudyResponse editarEstudio(String database, Integer cc, Integer professionId, StudyRequest request)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		RequestParser.requireBody(request);
		request.setDni(String.valueOf(cc));
		request.setProfessionId(String.valueOf(professionId));
		Study edited = studyInputPort(option).edit(cc, professionId, studyMapperRest.fromAdapterToDomain(request));
		return studyMapperRest.fromDomainToAdapterRest(edited, DatabaseResolver.label(option));
	}

	public Boolean eliminarEstudio(String database, Integer cc, Integer professionId)
			throws InvalidOptionException, NoExistException {
		return studyInputPort(DatabaseResolver.parse(database)).drop(cc, professionId);
	}

	public StudyResponse buscarEstudio(String database, Integer cc, Integer professionId)
			throws InvalidOptionException, NoExistException {
		DatabaseOption option = DatabaseResolver.parse(database);
		return studyMapperRest.fromDomainToAdapterRest(studyInputPort(option).findOne(cc, professionId),
				DatabaseResolver.label(option));
	}

	public Integer contarEstudios(String database) throws InvalidOptionException {
		return studyInputPort(DatabaseResolver.parse(database)).count();
	}

}
