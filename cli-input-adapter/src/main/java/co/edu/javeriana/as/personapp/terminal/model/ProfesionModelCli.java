package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Modelo de presentacion de Profesion en consola. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionModelCli {
	private Integer id;
	private String nombre;
	private String descripcion;
}
