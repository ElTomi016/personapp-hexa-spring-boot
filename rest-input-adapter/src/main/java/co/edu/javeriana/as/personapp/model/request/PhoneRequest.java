package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Cuerpo JSON de entrada para Telefono. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {
	private String number;
	private String company;
	private String ownerDni;
	private String database;
}
