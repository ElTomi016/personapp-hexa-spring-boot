package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import lombok.Getter;
import lombok.Setter;

public class StudyResponse extends StudyRequest {

	@Getter
	@Setter
	private String status;

	public StudyResponse(String dni, String professionId, String graduationDate, String universityName,
			String database, String status) {
		super(dni, professionId, graduationDate, universityName, database);
		this.status = status;
	}
}
