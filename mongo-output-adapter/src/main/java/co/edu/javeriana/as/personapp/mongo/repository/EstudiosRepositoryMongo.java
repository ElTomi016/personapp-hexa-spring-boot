package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;

/** [A] Repositorio Mongo de Estudios; id texto cc-idProf. */
public interface EstudiosRepositoryMongo extends MongoRepository<EstudiosDocument, String> {
}
