package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

@Mapper
public class TelefonoMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

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

	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		phone.setCompany(telefonoEntity.getOper());
		phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
		return phone;
	}

	private Person validateOwner(PersonaEntity duenio) {
		if (duenio == null) {
			throw new IllegalStateException("The phone has no owner");
		}
		return personaMapperMaria.fromAdapterToDomainShallow(duenio);
	}
}
