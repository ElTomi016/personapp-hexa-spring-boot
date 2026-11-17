package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Profession;

/** Puerto de SALIDA de Profesion: implementado por professionOutputAdapterMaria/Mongo [Integrante A]. */
@Port
public interface ProfessionOutputPort {
	/** Contrato: guardar (crear o reemplazar) y devolver lo persistido. */
	public Profession save(Profession profession);
	/** Contrato: borrar; false si no existia. */
	public Boolean delete(Integer identification);
	/** Contrato: listar todos. */
	public List<Profession> find();
	/** Contrato: buscar por llave; null si no existe. */
	public Profession findById(Integer identification);
}
