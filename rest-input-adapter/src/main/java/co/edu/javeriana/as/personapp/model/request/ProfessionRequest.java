package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Cuerpo JSON de entrada para Profesion. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionRequest {
	private String id;
	private String name;
	private String description;
	private String database;
}
