package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

/** Puerto de ENTRADA de Estudios (contrato de casos de uso). */
@Port
public interface StudyInputPort {

	/** Contrato: inyecta el OutputPort (adaptador Maria o Mongo) que usara este caso de uso. */
	public void setPersintence(StudyOutputPort studyPersintence);

	/** Contrato: crea/guarda el registro validando referencias cuando aplica. */
	public Study create(Study study) throws NoExistException;

	/** Contrato: editar un registro existente. */
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException;

	/** Contrato: eliminar un registro existente. */
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	/** Contrato: devuelve todos los registros del OutputPort. */
	public List<Study> findAll();

	/** Contrato: busca uno por su llave; NoExistException si no existe. */
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException;

	/** Contrato: cantidad total de registros. */
	public Integer count();
}
