package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Cuerpo JSON de entrada para Persona. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequest {
	private String dni;
	private String firstName;
	private String lastName;
	private String age;
	private String sex;
	private String database;
}
