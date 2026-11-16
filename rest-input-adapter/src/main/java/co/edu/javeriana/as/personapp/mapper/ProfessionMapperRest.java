package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfessionResponse;

@Mapper
public class ProfessionMapperRest {

	public ProfessionResponse fromDomainToAdapterRest(Profession profession, String database) {
		return new ProfessionResponse(
				profession.getIdentification() == null ? null : profession.getIdentification() + "",
				profession.getName(),
				profession.getDescription(),
				database,
				"OK");
	}

	public Profession fromAdapterToDomain(ProfessionRequest request) throws InvalidOptionException {
		RequestParser.requireBody(request);
		Profession profession = new Profession();
		profession.setIdentification(RequestParser.requiredInteger("id", request.getId()));
		profession.setName(RequestParser.requiredText("name", request.getName()));
		profession.setDescription(RequestParser.optionalText(request.getDescription()));
		return profession;
	}
}
