package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada CLI de Profesion. No guarda estado: el caso de uso se arma en
 * cada llamada segun la base elegida en el menu.
 */
@Slf4j
@Adapter
public class ProfessionInputAdapterCli {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperCli profesionMapperCli;

	private ProfessionInputPort professionInputPort(DatabaseOption database) {
		return new ProfessionUseCase(
				database == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
	}

	public List<ProfesionModelCli> historial(String dbOption) throws InvalidOptionException {
		log.info("Into historial Profession in Input Adapter");
		return professionInputPort(DatabaseResolver.parse(dbOption)).findAll().stream()
				.map(profesionMapperCli::fromDomainToAdapterCli).collect(Collectors.toList());
	}

	public ProfesionModelCli crear(String dbOption, ProfesionModelCli model) throws InvalidOptionException {
		Profession created = professionInputPort(DatabaseResolver.parse(dbOption))
				.create(profesionMapperCli.fromAdapterCliToDomain(model));
		return profesionMapperCli.fromDomainToAdapterCli(created);
	}

	public ProfesionModelCli editar(String dbOption, Integer id, ProfesionModelCli model)
			throws InvalidOptionException, NoExistException {
		model.setId(id);
		Profession edited = professionInputPort(DatabaseResolver.parse(dbOption)).edit(id,
				profesionMapperCli.fromAdapterCliToDomain(model));
		return profesionMapperCli.fromDomainToAdapterCli(edited);
	}

	public Boolean eliminar(String dbOption, Integer id) throws InvalidOptionException, NoExistException {
		return professionInputPort(DatabaseResolver.parse(dbOption)).drop(id);
	}

	public ProfesionModelCli buscar(String dbOption, Integer id) throws InvalidOptionException, NoExistException {
		return profesionMapperCli
				.fromDomainToAdapterCli(professionInputPort(DatabaseResolver.parse(dbOption)).findOne(id));
	}

	public Integer contar(String dbOption) throws InvalidOptionException {
		return professionInputPort(DatabaseResolver.parse(dbOption)).count();
	}

}
