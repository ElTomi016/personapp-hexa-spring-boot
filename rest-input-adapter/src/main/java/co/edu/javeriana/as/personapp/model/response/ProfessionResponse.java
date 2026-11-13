package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import lombok.Getter;
import lombok.Setter;

public class ProfessionResponse extends ProfessionRequest {

	@Getter
	@Setter
	private String status;

	public ProfessionResponse(String id, String name, String description, String database, String status) {
		super(id, name, description, database);
		this.status = status;
	}
}
