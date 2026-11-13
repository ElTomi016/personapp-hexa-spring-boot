package co.edu.javeriana.as.personapp.mongo.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import co.edu.javeriana.as.personapp.mongo.ItApplication;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

/**
 * Prueba de integracion contra la base levantada con docker compose (datos de
 * persona_dml). Se ejecuta con: mvn test -Dit=true
 */
@EnabledIfSystemProperty(named = "it", matches = "true")
@SpringBootTest(classes = ItApplication.class)
class AdaptersMongoIntegrationTest {

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personPort;
	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionPort;
	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phonePort;
	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyPort;

	private static Person ref(int cc) {
		Person person = new Person();
		person.setIdentification(cc);
		return person;
	}

	private static Profession profRef(int id) {
		Profession profession = new Profession();
		profession.setIdentification(id);
		return profession;
	}

	@Test
	void personsComeWithPhonesAndStudies() {
		Person pepe = personPort.findById(123456789);
		assertNotNull(pepe);
		assertEquals(2, pepe.getPhoneNumbers().size());
		assertEquals(2, pepe.getStudies().size());
		assertEquals(123456789, pepe.getPhoneNumbers().get(0).getOwner().getIdentification());
		assertTrue(pepe.getStudies().stream().allMatch(s -> s != null && s.getProfession().getName() != null));
		Person pepito = personPort.findById(987654321);
		assertTrue(pepito.getPhoneNumbers().isEmpty());
		assertTrue(pepito.getStudies().isEmpty());
		List<Person> all = personPort.find();
		assertTrue(all.size() >= 5);
		pepe.hashCode();
	}

	@Test
	void professionsComeWithStudies() {
		Profession ingeniero = professionPort.findById(1);
		assertEquals(2, ingeniero.getStudies().size());
		assertTrue(professionPort.find().size() >= 4);
		assertNull(professionPort.findById(99999));
	}

	@Test
	void professionCrud() {
		Profession created = new Profession(900, "Prueba");
		created.setDescription("desc");
		Profession saved = professionPort.save(created);
		try {
			assertEquals("Prueba", saved.getName());
			created.setName("Prueba2");
			assertEquals("Prueba2", professionPort.save(created).getName());
			assertEquals("Prueba2", professionPort.findById(900).getName());
		} finally {
			assertTrue(professionPort.delete(900));
		}
		assertNull(professionPort.findById(900));
		assertFalse(professionPort.delete(900));
	}

	@Test
	void phoneCrud() {
		Phone phone = new Phone("3990000001", "Claro", ref(987654321));
		try {
			Phone saved = phonePort.save(phone);
			assertEquals("Claro", saved.getCompany());
			assertEquals("Pepito", saved.getOwner().getFirstName());
			assertEquals(1, personPort.findById(987654321).getPhoneNumbers().size());
			phone.setCompany("Tigo");
			assertEquals("Tigo", phonePort.save(phone).getCompany());
			assertEquals("Tigo", phonePort.findById("3990000001").getCompany());
			assertTrue(phonePort.find().stream().anyMatch(p -> p.getNumber().equals("3990000001")));
		} finally {
			assertTrue(phonePort.delete("3990000001"));
		}
		assertNull(phonePort.findById("3990000001"));
	}

	@Test
	void studyCrud() {
		Study study = new Study(ref(987654321), profRef(4), LocalDate.of(2022, 3, 4), "Uni Prueba");
		try {
			Study saved = studyPort.save(study);
			assertEquals("Uni Prueba", saved.getUniversityName());
			assertEquals(LocalDate.of(2022, 3, 4), saved.getGraduationDate());
			assertEquals("Pepito", saved.getPerson().getFirstName());
			assertEquals("Arquitecto", saved.getProfession().getName());
			assertEquals(1, personPort.findById(987654321).getStudies().size());
			assertNotNull(studyPort.findById(987654321, 4));
			assertTrue(studyPort.find().size() >= 5);
		} finally {
			assertTrue(studyPort.delete(987654321, 4));
		}
		assertNull(studyPort.findById(987654321, 4));
	}

	@Test
	void personWithChildrenCreatedAndDeleted() {
		Person person = new Person(555000111, "Nueva", "Persona", Gender.FEMALE);
		person.setAge(22);
		Profession profession = profRef(3);
		Study study = new Study(person, profession, LocalDate.of(2021, 1, 2), "Uni");
		person.setStudies(new ArrayList<>(Arrays.asList(study)));
		person.setPhoneNumbers(new ArrayList<>(Arrays.asList(new Phone("3990000002", "WOM", person))));
		try {
			Person saved = personPort.save(person);
			assertEquals(1, saved.getPhoneNumbers().size());
			assertEquals(1, saved.getStudies().size());
			assertEquals("Abogado", saved.getStudies().get(0).getProfession().getName());
		} finally {
			assertTrue(personPort.delete(555000111));
		}
		assertNull(personPort.findById(555000111));
		assertNull(phonePort.findById("3990000002"));
		assertNull(studyPort.findById(555000111, 3));
	}
}
