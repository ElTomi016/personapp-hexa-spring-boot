package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

/** [Integrante A] Repositorio JPA de Estudios; id compuesto EstudiosEntityPK. */
public interface EstudiosRepositoryMaria extends JpaRepository<EstudiosEntity, EstudiosEntityPK> {
}
