package co.edu.javeriana.as.personapp.application.port.out;

import java.util.List;

import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.domain.Phone;

/** Puerto de SALIDA de Telefono: implementado por phoneOutputAdapterMaria/Mongo [Integrante A]. */
@Port
public interface PhoneOutputPort {
	/** Contrato: guardar (crear o reemplazar) y devolver lo persistido. */
	public Phone save(Phone phone);
	/** Contrato: borrar; false si no existia. */
	public Boolean delete(String number);
	/** Contrato: listar todos. */
	public List<Phone> find();
	/** Contrato: buscar por llave; null si no existe. */
	public Phone findById(String number);
}
