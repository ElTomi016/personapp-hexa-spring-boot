package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;

/** Puerto de ENTRADA de Profesion (contrato de casos de uso). */
@Port
public interface ProfessionInputPort {

	/** Contrato: inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	public void setPersintence(ProfessionOutputPort professionPersintence);

	/** Contrato: crea/guarda el registro validando referencias cuando aplica. */
	public Profession create(Profession profession);

	/** Contrato: editar un registro existente. */
	public Profession edit(Integer identification, Profession profession) throws NoExistException;

	/** Contrato: eliminar un registro existente. */
	public Boolean drop(Integer identification) throws NoExistException;

	/** Contrato: devuelve todos los registros del OutputPort. */
	public List<Profession> findAll();

	/** Contrato: busca uno por su llave; NoExistException si no existe. */
	public Profession findOne(Integer identification) throws NoExistException;

	/** Contrato: cantidad total de registros. */
	public Integer count();
}
