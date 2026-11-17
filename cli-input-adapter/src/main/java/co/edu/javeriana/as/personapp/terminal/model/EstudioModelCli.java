package co.edu.javeriana.as.personapp.terminal.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Modelo de presentacion de Estudios en consola. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	private Integer cc;
	private Integer idProfesion;
	private LocalDate fecha;
	private String universidad;
}
