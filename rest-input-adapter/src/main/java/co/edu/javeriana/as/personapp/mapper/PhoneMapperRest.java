package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;

@Mapper
public class PhoneMapperRest {

	public PhoneResponse fromDomainToAdapterRest(Phone phone, String database) {
		Person owner = phone.getOwner();
		return new PhoneResponse(
				phone.getNumber(),
				phone.getCompany(),
				owner == null || owner.getIdentification() == null ? null : owner.getIdentification() + "",
				database,
				"OK");
	}

	/** El dueno se arma solo con su identificacion; el adaptador de salida lo resuelve por id. */
	public Phone fromAdapterToDomain(PhoneRequest request) throws InvalidOptionException {
		RequestParser.requireBody(request);
		Person owner = new Person();
		owner.setIdentification(RequestParser.requiredInteger("ownerDni", request.getOwnerDni()));
		Phone phone = new Phone();
		phone.setNumber(RequestParser.requiredText("number", request.getNumber()));
		phone.setCompany(RequestParser.requiredText("company", request.getCompany()));
		phone.setOwner(owner);
		return phone;
	}
}
