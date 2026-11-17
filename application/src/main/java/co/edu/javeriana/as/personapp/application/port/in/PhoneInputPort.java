package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;

/** Puerto de ENTRADA de Telefono (contrato de casos de uso). */
@Port
public interface PhoneInputPort {

	/** Contrato: inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	public void setPersintence(PhoneOutputPort phonePersintence);

	/** Contrato: crea/guarda el registro validando referencias cuando aplica. */
	public Phone create(Phone phone) throws NoExistException;

	/** Contrato: editar un registro existente. */
	public Phone edit(String number, Phone phone) throws NoExistException;

	/** Contrato: eliminar un registro existente. */
	public Boolean drop(String number) throws NoExistException;

	/** Contrato: devuelve todos los registros del OutputPort. */
	public List<Phone> findAll();

	/** Contrato: busca uno por su llave; NoExistException si no existe. */
	public Phone findOne(String number) throws NoExistException;

	/** Contrato: cantidad total de registros. */
	public Integer count();
}
