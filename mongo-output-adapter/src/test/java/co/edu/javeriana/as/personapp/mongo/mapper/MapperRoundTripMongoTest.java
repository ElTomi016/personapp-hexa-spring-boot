package co.edu.javeriana.as.personapp.mongo.mapper;

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
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

class MapperRoundTripMongoTest {

	private PersonaMapperMongo personaMapper = new PersonaMapperMongo();
	private ProfesionMapperMongo profesionMapper = new ProfesionMapperMongo();
	private EstudiosMapperMongo estudiosMapper = new EstudiosMapperMongo();
	private TelefonoMapperMongo telefonoMapper = new TelefonoMapperMongo();

	@BeforeEach
	void wire() {
		ReflectionTestUtils.setField(personaMapper, "estudiosMapperMongo", estudiosMapper);
		ReflectionTestUtils.setField(personaMapper, "telefonoMapperMongo", telefonoMapper);
		ReflectionTestUtils.setField(telefonoMapper, "personaMapperMongo", personaMapper);
		ReflectionTestUtils.setField(estudiosMapper, "personaMapperMongo", personaMapper);
		ReflectionTestUtils.setField(estudiosMapper, "profesionMapperMongo", profesionMapper);
		ReflectionTestUtils.setField(profesionMapper, "estudiosMapperMongo", estudiosMapper);
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
		PersonaDocument doc = personaMapper.fromDomainToAdapter(newPerson());

		assertEquals(1, doc.getEstudios().size());
		assertEquals("123-1", doc.getEstudios().get(0).getId());
		assertEquals(123, doc.getEstudios().get(0).getPrimaryPersona().getId());
		assertEquals(1, doc.getTelefonos().size());
		assertEquals(123, doc.getTelefonos().get(0).getPrimaryDuenio().getId());

		// el lookup de Mongo devuelve documentos con referencias completas
		doc.getTelefonos().get(0).setPrimaryDuenio(doc);
		doc.getEstudios().get(0).setPrimaryPersona(doc);
		ProfesionDocument prof = new ProfesionDocument(1, "Ingeniera", "Sistemas", null);
		doc.getEstudios().get(0).setPrimaryProfesion(prof);

		Person back = personaMapper.fromAdapterToDomain(doc);

		assertEquals(123, back.getIdentification());
		assertEquals(1, back.getPhoneNumbers().size());
		assertEquals(123, back.getPhoneNumbers().get(0).getOwner().getIdentification());
		assertTrue(back.getPhoneNumbers().get(0).getOwner().getPhoneNumbers().isEmpty());
		assertEquals(1, back.getStudies().size());
		Study study = back.getStudies().get(0);
		assertNotNull(study);
		assertEquals("Javeriana", study.getUniversityName());
		assertEquals(LocalDate.of(2020, 5, 17), study.getGraduationDate());
		assertEquals("Ingeniera", study.getProfession().getName());
		back.hashCode();
	}

	@Test
	void professionWithStudiesRoundTrips() {
		EstudiosDocument estudio = estudiosMapper.fromDomainToAdapter(newPerson().getStudies().get(0));
		estudio.setPrimaryPersona(new PersonaDocument(123, "Ana", "Gomez", "F", 30, null, null));
		ProfesionDocument profesion = new ProfesionDocument(1, "Ingeniera", "Sistemas", new ArrayList<>());
		estudio.setPrimaryProfesion(profesion);
		profesion.getEstudios().add(estudio);
		Profession back = profesionMapper.fromAdapterToDomain(profesion);
		assertEquals(1, back.getStudies().size());
		assertEquals("Ana", back.getStudies().get(0).getPerson().getFirstName());
		back.hashCode();
	}

	@Test
	void phoneRoundTrip() {
		TelefonoDocument doc = telefonoMapper.fromDomainToAdapter(new Phone("300", "Claro", newPerson()));
		doc.setPrimaryDuenio(new PersonaDocument(123, "Ana", "Gomez", "F", 30, null, null));
		Phone back = telefonoMapper.fromAdapterToDomain(doc);
		assertEquals("Claro", back.getCompany());
		assertEquals("Ana", back.getOwner().getFirstName());
	}

	@Test
	void studyWithoutPersonIsRejected() {
		Study study = new Study();
		study.setProfession(new Profession(1, "X"));
		assertThrows(NullPointerException.class, () -> estudiosMapper.fromDomainToAdapter(study));
	}

	@Test
	void studyBuildIdIsCcDashProfession() {
		assertEquals("7-9", EstudiosMapperMongo.buildId(7, 9));
	}
}
