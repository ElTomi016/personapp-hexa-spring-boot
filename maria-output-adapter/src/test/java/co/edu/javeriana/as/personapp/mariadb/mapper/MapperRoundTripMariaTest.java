package co.edu.javeriana.as.personapp.mariadb.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

class MapperRoundTripMariaTest {

	private PersonaMapperMaria personaMapper = new PersonaMapperMaria();
	private ProfesionMapperMaria profesionMapper = new ProfesionMapperMaria();
	private EstudiosMapperMaria estudiosMapper = new EstudiosMapperMaria();
	private TelefonoMapperMaria telefonoMapper = new TelefonoMapperMaria();

	@BeforeEach
	void wire() {
		ReflectionTestUtils.setField(personaMapper, "estudiosMapperMaria", estudiosMapper);
		ReflectionTestUtils.setField(personaMapper, "telefonoMapperMaria", telefonoMapper);
		ReflectionTestUtils.setField(telefonoMapper, "personaMapperMaria", personaMapper);
		ReflectionTestUtils.setField(estudiosMapper, "personaMapperMaria", personaMapper);
		ReflectionTestUtils.setField(estudiosMapper, "profesionMapperMaria", profesionMapper);
		ReflectionTestUtils.setField(profesionMapper, "estudiosMapperMaria", estudiosMapper);
	}

	private Person newPerson() {
		Person person = new Person(123, "Ana", "Gomez", Gender.FEMALE);
		person.setAge(30);
		Profession profession = new Profession(1, "Ingeniera");
		profession.setDescription("Sistemas");
		Study study = new Study(person, profession);
		study.setGraduationDate(LocalDate.of(2020, 5, 17));
		study.setUniversityName("Javeriana");
		person.setStudies(new ArrayList<>(Arrays.asList(study)));
		person.setPhoneNumbers(new ArrayList<>(Arrays.asList(new Phone("300", "Claro", person))));
		return person;
	}

	@Test
	void personWithPhonesAndStudiesRoundTripsWithoutCycles() {
		PersonaEntity entity = personaMapper.fromDomainToAdapter(newPerson());

		assertEquals(1, entity.getEstudios().size());
		assertEquals(1, entity.getTelefonos().size());
		assertEquals(entity, entity.getTelefonos().get(0).getDuenio());
		assertEquals(entity, entity.getEstudios().get(0).getPersona());
		assertNotNull(entity.getEstudios().get(0).getProfesion());

		// JPA devuelve la profesion completa tras save()/find()
		entity.getEstudios().get(0).setProfesion(new ProfesionEntity(1, "Ingeniera"));

		Person back = personaMapper.fromAdapterToDomain(entity);

		assertEquals(123, back.getIdentification());
		assertEquals(Gender.FEMALE, back.getGender());
		assertEquals(1, back.getPhoneNumbers().size());
		assertEquals("300", back.getPhoneNumbers().get(0).getNumber());
		assertEquals(123, back.getPhoneNumbers().get(0).getOwner().getIdentification());
		assertTrue(back.getPhoneNumbers().get(0).getOwner().getPhoneNumbers().isEmpty());
		assertEquals(1, back.getStudies().size());
		Study study = back.getStudies().get(0);
		assertNotNull(study);
		assertEquals("Javeriana", study.getUniversityName());
		assertEquals(LocalDate.of(2020, 5, 17), study.getGraduationDate());
		assertEquals(123, study.getPerson().getIdentification());
		// hashCode recorre las listas: con ciclos habria StackOverflowError
		back.hashCode();
	}

	@Test
	void studyMapsBackWhenEntityComesFromSql() {
		EstudiosEntity entity = estudiosMapper.fromDomainToAdapter(newPerson().getStudies().get(0));
		entity.setFecha(java.sql.Date.valueOf("2020-05-17"));
		entity.setPersona(new PersonaEntity(123, "Ana", "Gomez", 'F'));
		entity.setProfesion(new ProfesionEntity(1, "Ingeniera"));
		Study back = estudiosMapper.fromAdapterToDomain(entity);
		assertEquals(LocalDate.of(2020, 5, 17), back.getGraduationDate());
		assertEquals(1, back.getProfession().getIdentification());
	}

	@Test
	void professionWithStudiesRoundTrips() {
		ProfesionEntity profesion = new ProfesionEntity(1, "Ingeniera");
		EstudiosEntity estudio = estudiosMapper.fromDomainToAdapter(newPerson().getStudies().get(0));
		estudio.setPersona(new PersonaEntity(123, "Ana", "Gomez", 'F'));
		estudio.setProfesion(profesion);
		profesion.setEstudios(new ArrayList<>(Arrays.asList(estudio)));
		Profession back = profesionMapper.fromAdapterToDomain(profesion);
		assertEquals(1, back.getStudies().size());
		assertEquals("Ana", back.getStudies().get(0).getPerson().getFirstName());
		back.hashCode();
	}

	@Test
	void nullGenderInEntityDoesNotThrow() {
		PersonaEntity entity = new PersonaEntity(5, "A", "B", null);
		assertEquals(Gender.OTHER, personaMapper.fromAdapterToDomain(entity).getGender());
	}

	@Test
	void genderOtherIsRejectedOnWrite() {
		Person person = new Person(5, "A", "B", Gender.OTHER);
		assertThrows(IllegalArgumentException.class, () -> personaMapper.fromDomainToAdapter(person));
	}

	@Test
	void phoneWithoutOwnerIsRejected() {
		Phone phone = new Phone();
		phone.setNumber("1");
		phone.setCompany("X");
		assertThrows(NullPointerException.class, () -> telefonoMapper.fromDomainToAdapter(phone));
	}

	@Test
	void phoneRoundTrip() {
		TelefonoEntity entity = telefonoMapper.fromDomainToAdapter(new Phone("300", "Claro", newPerson()));
		entity.setDuenio(new PersonaEntity(123, "Ana", "Gomez", 'F'));
		Phone back = telefonoMapper.fromAdapterToDomain(entity);
		assertEquals("Claro", back.getCompany());
		assertEquals("Ana", back.getOwner().getFirstName());
	}
}
