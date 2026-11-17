package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;

import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.PersonaMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

/**
 * [Integrante A] Adaptador de salida MariaDB para Persona (puerto PersonOutputPort).
 * Qualifier "personOutputAdapterMaria". Al ser @Transactional, las listas LAZY de
 * telefonos/estudios se cargan antes del mapeo => agregacion en Persona.
 */
@Slf4j
@Adapter("personOutputAdapterMaria")
@Transactional
public class PersonOutputAdapterMaria implements PersonOutputPort {

	@Autowired
	private PersonaRepositoryMaria personaRepositoryMaria;

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	/** Guarda (crea o reemplaza) y devuelve lo persistido. */
	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MariaDB");
		// [A] El mapper ya arma telefonos/estudios; el cascade de JPA los guarda.
		PersonaEntity persistedPersona = personaRepositoryMaria.save(personaMapperMaria.fromDomainToAdapter(person));
		return personaMapperMaria.fromAdapterToDomain(persistedPersona);
	}

	/** Borra por cc; false si no existe, true si quedo eliminado. */
	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MariaDB");
		if (!personaRepositoryMaria.existsById(identification)) {
			return false;
		}
		personaRepositoryMaria.deleteById(identification);
		return !personaRepositoryMaria.existsById(identification);
	}

	/** Lista todos los registros ya mapeados a dominio. */
	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MariaDB");
		return personaRepositoryMaria.findAll().stream().map(personaMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/** Busca por cc con una sola consulta; null si no existe. */
	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MariaDB");
		// [A] Una sola consulta (el original hacia existsById + findById).
		return personaRepositoryMaria.findById(identification).map(personaMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}
}
