package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import lombok.Getter;
import lombok.Setter;

public class PhoneResponse extends PhoneRequest {

	@Getter
	@Setter
	private String status;

	public PhoneResponse(String number, String company, String ownerDni, String database, String status) {
		super(number, company, ownerDni, database);
		this.status = status;
	}
}
