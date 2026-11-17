package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

/** [A] Repositorio Mongo de Telefono; id = numero (String). */
public interface TelefonoRepositoryMongo extends MongoRepository<TelefonoDocument, String> {
}
