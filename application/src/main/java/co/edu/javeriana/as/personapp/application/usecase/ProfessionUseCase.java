package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

/** [Integrante B] Casos de uso de Profesion: CRUD sobre ProfessionOutputPort. */
@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

	private ProfessionOutputPort professionPersintence;

	public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	/** Inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	@Override
	public void setPersintence(ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	/** Crea/guarda el registro validando referencias cuando aplica. */
	@Override
	public Profession create(Profession profession) {
		log.debug("Into create on Application Domain");
		return professionPersintence.save(profession);
	}

	/** Edita: verifica que exista y lo guarda; NoExistException si no. */
	@Override
	public Profession edit(Integer identification, Profession profession) throws NoExistException {
		Profession oldProfession = professionPersintence.findById(identification);
		if (oldProfession != null) {
			profession.setIdentification(identification);
			return professionPersintence.save(profession);
		}
		throw new NoExistException(
				"The profession with id " + identification + " does not exist into db, cannot be edited");
	}

	/** Elimina: verifica que exista y lo borra; NoExistException si no. */
	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		Profession oldProfession = professionPersintence.findById(identification);
		if (oldProfession != null)
			return professionPersintence.delete(identification);
		throw new NoExistException(
				"The profession with id " + identification + " does not exist into db, cannot be dropped");
	}

	/** Devuelve todos los registros del OutputPort. */
	@Override
	public List<Profession> findAll() {
		log.info("Output: " + professionPersintence.getClass());
		return professionPersintence.find();
	}

	/** Busca uno por su llave; NoExistException si no existe. */
	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession oldProfession = professionPersintence.findById(identification);
		if (oldProfession != null)
			return oldProfession;
		throw new NoExistException(
				"The profession with id " + identification + " does not exist into db, cannot be found");
	}

	/** Cantidad total de registros. */
	@Override
	public Integer count() {
		return findAll().size();
	}
}
