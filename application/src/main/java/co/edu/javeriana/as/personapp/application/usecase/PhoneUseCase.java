package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

/** [Integrante B] Casos de uso de Telefono: CRUD sobre PhoneOutputPort. */
@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	private PhoneOutputPort phonePersintence;

	// Se usa solo para comprobar que el dueno exista (Mongo no tiene claves foraneas).
	private final PersonOutputPort personPersintence;

	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersintence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		this.phonePersintence = phonePersintence;
		this.personPersintence = personPersintence;
	}

	/** Inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	@Override
	public void setPersintence(PhoneOutputPort phonePersintence) {
		this.phonePersintence = phonePersintence;
	}

	/** Crea/guarda el registro validando referencias cuando aplica. */
	@Override
	public Phone create(Phone phone) throws NoExistException {
		log.debug("Into create on Application Domain");
		validateOwner(phone);
		return phonePersintence.save(phone);
	}

	/** Edita: verifica que exista y lo guarda; NoExistException si no. */
	@Override
	public Phone edit(String number, Phone phone) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null) {
			validateOwner(phone);
			phone.setNumber(number);
			return phonePersintence.save(phone);
		}
		throw new NoExistException("The phone with number " + number + " does not exist into db, cannot be edited");
	}

	/** Elimina: verifica que exista y lo borra; NoExistException si no. */
	@Override
	public Boolean drop(String number) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null)
			return phonePersintence.delete(number);
		throw new NoExistException("The phone with number " + number + " does not exist into db, cannot be dropped");
	}

	/** Devuelve todos los registros del OutputPort. */
	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersintence.getClass());
		return phonePersintence.find();
	}

	/** Busca uno por su llave; NoExistException si no existe. */
	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null)
			return oldPhone;
		throw new NoExistException("The phone with number " + number + " does not exist into db, cannot be found");
	}

	/** Cantidad total de registros. */
	@Override
	public Integer count() {
		return findAll().size();
	}

	/** Exige que el telefono tenga dueno y que la persona exista. */
	private void validateOwner(Phone phone) throws NoExistException {
		Person owner = phone.getOwner();
		if (owner == null || owner.getIdentification() == null) {
			throw new NoExistException("The phone has no owner, cannot be saved");
		}
		if (personPersintence.findById(owner.getIdentification()) == null) {
			throw new NoExistException("The person with id " + owner.getIdentification()
					+ " does not exist into db, cannot be the owner of the phone");
		}
	}
}
