package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

/**
 * [Integrante A] Traduce Phone (dominio) <-> TelefonoEntity (MariaDB).
 * BUG ORIGINAL: el dueno se mapeaba con la persona completa (con sus telefonos)
 * -> recursion infinita. Ahora el dueno se mapea shallow.
 */
@Mapper
public class TelefonoMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	/** Dominio -> entidad JPA de telefono. */
	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		Objects.requireNonNull(phone, "phone is required");
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		telefonoEntity.setDuenio(validateDuenio(phone.getOwner()));
		return telefonoEntity;
	}

	/** El dueno se referencia solo por su cc; JPA resuelve el resto al guardar. */
	private PersonaEntity validateDuenio(Person owner) {
		Objects.requireNonNull(owner, "the phone owner is required");
		return new PersonaEntity(owner.getIdentification());
	}

	/** Entidad -> dominio; el dueno llega shallow. */
	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		phone.setCompany(telefonoEntity.getOper());
		phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
		return phone;
	}

	/** Exige dueno y lo mapea shallow para no ciclar. */
	private Person validateOwner(PersonaEntity duenio) {
		if (duenio == null) {
			throw new IllegalStateException("The phone has no owner");
		}
		// Shallow: sin lista de telefonos, corta el ciclo.
		return personaMapperMaria.fromAdapterToDomainShallow(duenio);
	}
}
