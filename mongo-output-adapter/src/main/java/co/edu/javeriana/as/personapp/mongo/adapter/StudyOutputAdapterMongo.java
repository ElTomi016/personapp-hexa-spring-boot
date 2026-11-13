package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {

	@Autowired
	private EstudiosRepositoryMongo estudiosRepositoryMongo;

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	@Override
	public Study save(Study study) {
		log.debug("Into save on Adapter MongoDB (study)");
		estudiosRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
		return findById(study.getPerson().getIdentification(), study.getProfession().getIdentification());
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into delete on Adapter MongoDB (study)");
		String id = EstudiosMapperMongo.buildId(personIdentification, professionIdentification);
		if (!estudiosRepositoryMongo.existsById(id)) {
			return false;
		}
		estudiosRepositoryMongo.deleteById(id);
		return !estudiosRepositoryMongo.existsById(id);
	}

	@Override
	public List<Study> find() {
		log.debug("Into find on Adapter MongoDB (study)");
		return estudiosRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		log.debug("Into findById on Adapter MongoDB (study)");
		return estudiosRepositoryMongo
				.findById(EstudiosMapperMongo.buildId(personIdentification, professionIdentification))
				.map(estudiosMapperMongo::fromAdapterToDomain).orElse(null);
	}
}
