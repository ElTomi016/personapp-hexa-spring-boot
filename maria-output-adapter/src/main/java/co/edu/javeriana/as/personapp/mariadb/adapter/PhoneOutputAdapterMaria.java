package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.TelefonoMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

/**
 * [Integrante A] Adaptador de salida MariaDB para Telefono (PhoneOutputPort).
 * Qualifier "phoneOutputAdapterMaria". Implementado por nosotros (no existia).
 * delete devuelve false si el telefono no existe; findById hace una sola consulta.
 */
@Slf4j
@Adapter("phoneOutputAdapterMaria")
@Transactional
public class PhoneOutputAdapterMaria implements PhoneOutputPort {

	@Autowired
	private TelefonoRepositoryMaria telefonoRepositoryMaria;

	@Autowired
	private TelefonoMapperMaria telefonoMapperMaria;

	/** Guarda (crea o reemplaza) y devuelve lo persistido. */
	@Override
	public Phone save(Phone phone) {
		log.debug("Into save on Adapter MariaDB (phone)");
		TelefonoEntity persisted = telefonoRepositoryMaria.save(telefonoMapperMaria.fromDomainToAdapter(phone));
		return telefonoMapperMaria.fromAdapterToDomain(persisted);
	}

	/** Borra por numero; false si no existe, true si quedo eliminado. */
	@Override
	public Boolean delete(String number) {
		log.debug("Into delete on Adapter MariaDB (phone)");
		if (!telefonoRepositoryMaria.existsById(number)) {
			return false;
		}
		telefonoRepositoryMaria.deleteById(number);
		return !telefonoRepositoryMaria.existsById(number);
	}

	/** Lista todos los registros ya mapeados a dominio. */
	@Override
	public List<Phone> find() {
		log.debug("Into find on Adapter MariaDB (phone)");
		return telefonoRepositoryMaria.findAll().stream().map(telefonoMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	/** Busca por numero con una sola consulta; null si no existe. */
	@Override
	public Phone findById(String number) {
		log.debug("Into findById on Adapter MariaDB (phone)");
		return telefonoRepositoryMaria.findById(number).map(telefonoMapperMaria::fromAdapterToDomain).orElse(null);
	}
}
