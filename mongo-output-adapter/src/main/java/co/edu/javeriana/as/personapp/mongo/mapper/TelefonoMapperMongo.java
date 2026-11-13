package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

@Mapper
public class TelefonoMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		Objects.requireNonNull(phone, "phone is required");
		TelefonoDocument telefonoDocument = new TelefonoDocument();
		telefonoDocument.setId(phone.getNumber());
		telefonoDocument.setOper(phone.getCompany());
		telefonoDocument.setPrimaryDuenio(validateDuenio(phone.getOwner()));
		return telefonoDocument;
	}

	/** Referencia por id: @DocumentReference solo almacena el _id del dueno. */
	private PersonaDocument validateDuenio(Person owner) {
		Objects.requireNonNull(owner, "the phone owner is required");
		PersonaDocument duenio = new PersonaDocument();
		duenio.setId(owner.getIdentification());
		return duenio;
	}

	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		Phone phone = new Phone();
		phone.setNumber(telefonoDocument.getId());
		phone.setCompany(telefonoDocument.getOper());
		phone.setOwner(validateOwner(telefonoDocument.getPrimaryDuenio()));
		return phone;
	}

	private Person validateOwner(PersonaDocument duenio) {
		if (duenio == null) {
			throw new IllegalStateException("The phone has no owner");
		}
		return personaMapperMongo.fromAdapterToDomainShallow(duenio);
	}
}
