package co.edu.javeriana.as.personapp.mariadb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

/** [Integrante A] Repositorio JPA de Telefono; id = numero (String). */
public interface TelefonoRepositoryMaria extends JpaRepository<TelefonoEntity, String> {
}
