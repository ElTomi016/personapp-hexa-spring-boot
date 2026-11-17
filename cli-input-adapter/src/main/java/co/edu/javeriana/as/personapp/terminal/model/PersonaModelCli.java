package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Modelo de presentacion de Persona en consola. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaModelCli {
	private Integer cc;
	private String nombre;
	private String apellido;
	private String genero;
	private Integer edad;
}
