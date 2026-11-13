package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("personOutputAdapterMongo")
public class PersonOutputAdapterMongo implements PersonOutputPort {

	@Autowired
	private PersonaRepositoryMongo personaRepositoryMongo;

	@Autowired
	private TelefonoRepositoryMongo telefonoRepositoryMongo;

	@Autowired
	private EstudiosRepositoryMongo estudiosRepositoryMongo;

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Las listas de telefonos y estudios de PersonaDocument son de solo lectura
	 * (lookup), por eso cada hijo se guarda en su propia coleccion. Los errores de
	 * escritura de Mongo se propagan al llamador.
	 */
	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MongoDB");
		PersonaDocument document = personaMapperMongo.fromDomainToAdapter(person);
		personaRepositoryMongo.save(document);
		for (TelefonoDocument telefono : document.getTelefonos()) {
			telefonoRepositoryMongo.save(telefono);
		}
		for (EstudiosDocument estudio : document.getEstudios()) {
			estudiosRepositoryMongo.save(estudio);
		}
		return findById(person.getIdentification());
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		if (!personaRepositoryMongo.existsById(identification)) {
			return false;
		}
		// Mongo no tiene FK ni cascade: se eliminan los hijos que referencian a la persona.
		mongoTemplate.remove(Query.query(Criteria.where("primaryDuenio").is(identification)), TelefonoDocument.class);
		mongoTemplate.remove(Query.query(Criteria.where("primaryPersona").is(identification)), EstudiosDocument.class);
		personaRepositoryMongo.deleteById(identification);
		return !personaRepositoryMongo.existsById(identification);
	}

	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MongoDB");
		return personaRepositoryMongo.findAll().stream().map(personaMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return personaRepositoryMongo.findById(identification).map(personaMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}
}
