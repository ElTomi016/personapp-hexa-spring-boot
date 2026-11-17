package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Person;

/** Puerto de SALIDA de Persona: lo implementan los adaptadores Maria y Mongo (personOutputAdapterMaria/Mongo). */
@Port
public interface PersonOutputPort {
	public Person save(Person person);
	public Boolean delete(Integer identification);
	public List<Person> find();
	public Person findById(Integer identification);
}
