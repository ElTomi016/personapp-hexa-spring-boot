package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** [Integrante B] Cuerpo JSON de entrada para Estudios. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRequest {
	private String dni;
	private String professionId;
	private String graduationDate;
	private String universityName;
	private String database;
}
