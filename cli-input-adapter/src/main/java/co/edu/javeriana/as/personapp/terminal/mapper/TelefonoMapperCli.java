package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		TelefonoModelCli telefonoModelCli = new TelefonoModelCli();
		telefonoModelCli.setNumero(phone.getNumber());
		telefonoModelCli.setOperador(phone.getCompany());
		telefonoModelCli.setDuenio(phone.getOwner() == null ? null : phone.getOwner().getIdentification());
		return telefonoModelCli;
	}

	/** El dueno se arma solo con su cedula; el caso de uso comprueba que exista. */
	public Phone fromAdapterCliToDomain(TelefonoModelCli telefonoModelCli) {
		Person owner = new Person();
		owner.setIdentification(telefonoModelCli.getDuenio());
		Phone phone = new Phone();
		phone.setNumber(telefonoModelCli.getNumero());
		phone.setCompany(telefonoModelCli.getOperador());
		phone.setOwner(owner);
		return phone;
	}
}
