package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;

/** Repositorio Mongo de Persona (base original); id = cc. */
public interface PersonaRepositoryMongo extends MongoRepository<PersonaDocument, Integer> {

}
