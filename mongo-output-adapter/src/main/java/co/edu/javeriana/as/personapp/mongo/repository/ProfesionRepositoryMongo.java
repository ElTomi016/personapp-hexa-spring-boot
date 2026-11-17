package co.edu.javeriana.as.personapp.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;

/** [A] Repositorio Mongo de Profesion; id Integer. */
public interface ProfesionRepositoryMongo extends MongoRepository<ProfesionDocument, Integer> {
}
