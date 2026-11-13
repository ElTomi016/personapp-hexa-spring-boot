package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

@Mapper
public class PersonaMapperMaria {

	@Autowired
	private EstudiosMapperMaria estudiosMapperMaria;

	@Autowired
	private TelefonoMapperMaria telefonoMapperMaria;

	/**
	 * Conversion completa: incluye telefonos y estudios. Las referencias hacia la
	 * persona dentro de cada hijo apuntan a la entidad que se esta construyendo,
	 * de modo que el cascade de JPA tenga el lado propietario completo.
	 */
	public PersonaEntity fromDomainToAdapter(Person person) {
		Objects.requireNonNull(person, "person is required");
		PersonaEntity personaEntity = new PersonaEntity();
		personaEntity.setCc(person.getIdentification());
		personaEntity.setNombre(person.getFirstName());
		personaEntity.setApellido(person.getLastName());
		personaEntity.setGenero(validateGenero(person.getGender()));
		personaEntity.setEdad(validateEdad(person.getAge()));
		personaEntity.setEstudios(validateEstudios(person.getStudies(), personaEntity));
		personaEntity.setTelefonos(validateTelefonos(person.getPhoneNumbers(), personaEntity));
		return personaEntity;
	}

	private Character validateGenero(Gender gender) {
		if (gender == Gender.FEMALE) {
			return 'F';
		}
		if (gender == Gender.MALE) {
			return 'M';
		}
		throw new IllegalArgumentException("Gender " + gender + " is not supported by MariaDB (only M or F)");
	}

	private Integer validateEdad(Integer age) {
		return age != null && age >= 0 ? age : null;
	}

	private List<EstudiosEntity> validateEstudios(List<Study> studies, PersonaEntity owner) {
		if (studies == null || studies.isEmpty()) {
			return new ArrayList<EstudiosEntity>();
		}
		return studies.stream().map(study -> {
			EstudiosEntity estudio = estudiosMapperMaria.fromDomainToAdapter(study);
			estudio.setPersona(owner);
			return estudio;
		}).collect(Collectors.toList());
	}

	private List<TelefonoEntity> validateTelefonos(List<Phone> phoneNumbers, PersonaEntity owner) {
		if (phoneNumbers == null || phoneNumbers.isEmpty()) {
			return new ArrayList<TelefonoEntity>();
		}
		return phoneNumbers.stream().map(phone -> {
			TelefonoEntity telefono = telefonoMapperMaria.fromDomainToAdapter(phone);
			telefono.setDuenio(owner);
			return telefono;
		}).collect(Collectors.toList());
	}

	/** Conversion completa: incluye telefonos y estudios (con persona y profesion superficiales). */
	public Person fromAdapterToDomain(PersonaEntity personaEntity) {
		Person person = fromAdapterToDomainShallow(personaEntity);
		person.setStudies(validateStudies(personaEntity.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaEntity.getTelefonos()));
		return person;
	}

	/** Solo datos propios de la persona, sin listas. Se usa para cortar los ciclos. */
	public Person fromAdapterToDomainShallow(PersonaEntity personaEntity) {
		Person person = new Person();
		person.setIdentification(personaEntity.getCc());
		person.setFirstName(personaEntity.getNombre());
		person.setLastName(personaEntity.getApellido());
		person.setGender(validateGender(personaEntity.getGenero()));
		person.setAge(validateAge(personaEntity.getEdad()));
		person.setStudies(new ArrayList<Study>());
		person.setPhoneNumbers(new ArrayList<Phone>());
		return person;
	}

	private Gender validateGender(Character genero) {
		return Character.valueOf('F').equals(genero) ? Gender.FEMALE
				: Character.valueOf('M').equals(genero) ? Gender.MALE : Gender.OTHER;
	}

	private Integer validateAge(Integer edad) {
		return edad != null && edad >= 0 ? edad : null;
	}

	private List<Study> validateStudies(List<EstudiosEntity> estudiosEntity) {
		return estudiosEntity != null && !estudiosEntity.isEmpty() ? estudiosEntity.stream()
				.map(estudio -> estudiosMapperMaria.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoEntity> telefonoEntities) {
		return telefonoEntities != null && !telefonoEntities.isEmpty() ? telefonoEntities.stream()
				.map(telefono -> telefonoMapperMaria.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}
