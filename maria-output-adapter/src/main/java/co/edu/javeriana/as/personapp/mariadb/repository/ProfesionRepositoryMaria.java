package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

/** [Integrante A] Repositorio JPA de Profesion; id Integer. */
public interface ProfesionRepositoryMaria extends JpaRepository<ProfesionEntity, Integer> {
}
