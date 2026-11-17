package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

/**
 * [Integrante A] Phone (dominio) <-> TelefonoDocument (MongoDB).
 * BUG ORIGINAL: el dueno se mapeaba completo (con sus telefonos) => recursion infinita.
 */
@Mapper
public class TelefonoMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	/** Dominio -> documento; _id = numero de telefono. */
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

	/** Documento -> dominio; el dueno llega shallow. */
	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		Phone phone = new Phone();
		phone.setNumber(telefonoDocument.getId());
		phone.setCompany(telefonoDocument.getOper());
		phone.setOwner(validateOwner(telefonoDocument.getPrimaryDuenio()));
		return phone;
	}

	/** Exige dueno y lo mapea shallow para no ciclar. */
	private Person validateOwner(PersonaDocument duenio) {
		if (duenio == null) {
			throw new IllegalStateException("The phone has no owner");
		}
		// Shallow: sin lista de telefonos, corta el ciclo.
		return personaMapperMongo.fromAdapterToDomainShallow(duenio);
	}
}
