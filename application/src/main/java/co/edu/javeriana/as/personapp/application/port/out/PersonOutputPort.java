package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Person;

/** Puerto de SALIDA de Persona: lo implementan los adaptadores Maria y Mongo (personOutputAdapterMaria/Mongo). */
@Port
public interface PersonOutputPort {
	/** Contrato: guardar (crear o reemplazar) y devolver lo persistido. */
	public Person save(Person person);
	/** Contrato: borrar; false si no existia. */
	public Boolean delete(Integer identification);
	/** Contrato: listar todos. */
	public List<Person> find();
	/** Contrato: buscar por llave; null si no existe. */
	public Person findById(Integer identification);
}
