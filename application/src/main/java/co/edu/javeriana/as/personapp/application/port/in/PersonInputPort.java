package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;

/** Puerto de ENTRADA de Persona: lo que los adaptadores REST/CLI pueden pedirle a la aplicacion. */
@Port
public interface PersonInputPort {
	
	/** Contrato: inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	public void setPersintence(PersonOutputPort personPersintence);
	
	/** Contrato: crea/guarda el registro validando referencias cuando aplica. */
	public Person create(Person person);

	/** Contrato: editar un registro existente. */
	public Person edit(Integer identification, Person person) throws NoExistException;

	/** Contrato: eliminar un registro existente. */
	public Boolean drop(Integer identification) throws NoExistException;

	/** Contrato: devuelve todos los registros del OutputPort. */
	public List<Person> findAll();

	/** Contrato: busca uno por su llave; NoExistException si no existe. */
	public Person findOne(Integer identification) throws NoExistException;

	/** Contrato: cantidad total de registros. */
	public Integer count();

	/** Contrato: telefonos de la persona (ya cargados por el adaptador de salida). */
	public List<Phone> getPhones(Integer identification) throws NoExistException;

	/** Contrato: estudios de la persona (ya cargados por el adaptador de salida). */
	public List<Study> getStudies(Integer identification) throws NoExistException;
}
