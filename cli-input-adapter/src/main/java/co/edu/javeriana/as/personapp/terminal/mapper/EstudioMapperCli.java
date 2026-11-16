package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		EstudioModelCli estudioModelCli = new EstudioModelCli();
		estudioModelCli.setCc(study.getPerson() == null ? null : study.getPerson().getIdentification());
		estudioModelCli.setIdProfesion(study.getProfession() == null ? null : study.getProfession().getIdentification());
		estudioModelCli.setFecha(study.getGraduationDate());
		estudioModelCli.setUniversidad(study.getUniversityName());
		return estudioModelCli;
	}

	/** Persona y profesion se arman solo con su identificacion; el caso de uso comprueba que existan. */
	public Study fromAdapterCliToDomain(EstudioModelCli estudioModelCli) {
		Person person = new Person();
		person.setIdentification(estudioModelCli.getCc());
		Profession profession = new Profession();
		profession.setIdentification(estudioModelCli.getIdProfesion());
		Study study = new Study(person, profession);
		study.setGraduationDate(estudioModelCli.getFecha());
		study.setUniversityName(estudioModelCli.getUniversidad());
		return study;
	}
}
