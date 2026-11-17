package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Study;

/** Puerto de SALIDA de Estudios: implementado por studyOutputAdapterMaria/Mongo [Integrante A]. */
@Port
public interface StudyOutputPort {
	/** Contrato: guardar (crear o reemplazar) y devolver lo persistido. */
	public Study save(Study study);
	/** Contrato: borrar; false si no existia. */
	public Boolean delete(Integer personIdentification, Integer professionIdentification);
	/** Contrato: listar todos. */
	public List<Study> find();
	/** Contrato: buscar por llave; null si no existe. */
	public Study findById(Integer personIdentification, Integer professionIdentification);
}
