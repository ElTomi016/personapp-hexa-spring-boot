package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada CLI de Estudios (clave compuesta persona + profesion). No
 * guarda estado: el caso de uso se arma en cada llamada segun la base elegida.
 */
@Slf4j
@Adapter
public class StudyInputAdapterCli {

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	private StudyInputPort studyInputPort(DatabaseOption database) {
		return database == DatabaseOption.MARIA
				? new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria)
				: new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
	}

	public List<EstudioModelCli> historial(String dbOption) throws InvalidOptionException {
		log.info("Into historial Study in Input Adapter");
		return studyInputPort(DatabaseResolver.parse(dbOption)).findAll().stream()
				.map(estudioMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public EstudioModelCli crear(String dbOption, EstudioModelCli model)
			throws InvalidOptionException, NoExistException {
		Study created = studyInputPort(DatabaseResolver.parse(dbOption))
				.create(estudioMapperCli.fromAdapterCliToDomain(model));
		return estudioMapperCli.fromDomainToAdapterCli(created);
	}

	public EstudioModelCli editar(String dbOption, Integer cc, Integer idProfesion, EstudioModelCli model)
			throws InvalidOptionException, NoExistException {
		model.setCc(cc);
		model.setIdProfesion(idProfesion);
		Study edited = studyInputPort(DatabaseResolver.parse(dbOption)).edit(cc, idProfesion,
				estudioMapperCli.fromAdapterCliToDomain(model));
		return estudioMapperCli.fromDomainToAdapterCli(edited);
	}

	public Boolean eliminar(String dbOption, Integer cc, Integer idProfesion)
			throws InvalidOptionException, NoExistException {
		return studyInputPort(DatabaseResolver.parse(dbOption)).drop(cc, idProfesion);
	}

	public EstudioModelCli buscar(String dbOption, Integer cc, Integer idProfesion)
			throws InvalidOptionException, NoExistException {
		return estudioMapperCli
				.fromDomainToAdapterCli(studyInputPort(DatabaseResolver.parse(dbOption)).findOne(cc, idProfesion));
	}

	public Integer contar(String dbOption) throws InvalidOptionException {
		return studyInputPort(DatabaseResolver.parse(dbOption)).count();
	}

}
