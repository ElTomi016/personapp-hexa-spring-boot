package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

/** [Integrante A] Adaptador de salida MongoDB para Telefono (qualifier "phoneOutputAdapterMongo"). */
@Slf4j
@Adapter("phoneOutputAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

	@Autowired
	private TelefonoRepositoryMongo telefonoRepositoryMongo;

	@Autowired
	private TelefonoMapperMongo telefonoMapperMongo;

	/** Guarda (crea o reemplaza) y devuelve lo persistido. */
	@Override
	public Phone save(Phone phone) {
		log.debug("Into save on Adapter MongoDB (phone)");
		telefonoRepositoryMongo.save(telefonoMapperMongo.fromDomainToAdapter(phone));
		// [A] Se relee para devolver lo realmente guardado.
		/** Busca por numero con una sola consulta; null si no existe. */
		return findById(phone.getNumber());
	}

	/** Borra por numero; false si no existe, true si quedo eliminado. */
	@Override
	public Boolean delete(String number) {
		log.debug("Into delete on Adapter MongoDB (phone)");
		if (!telefonoRepositoryMongo.existsById(number)) {
			return false;
		}
		telefonoRepositoryMongo.deleteById(number);
		return !telefonoRepositoryMongo.existsById(number);
	}

	/** Lista todos los registros ya mapeados a dominio. */
	@Override
	public List<Phone> find() {
		log.debug("Into find on Adapter MongoDB (phone)");
		return telefonoRepositoryMongo.findAll().stream().map(telefonoMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Phone findById(String number) {
		log.debug("Into findById on Adapter MongoDB (phone)");
		return telefonoRepositoryMongo.findById(number).map(telefonoMapperMongo::fromAdapterToDomain).orElse(null);
	}
}
