package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersintence;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence) {
		this.studyPersintence = studyPersintence;
	}

	@Override
	public void setPersintence(StudyOutputPort studyPersintence) {
		this.studyPersintence = studyPersintence;
	}

	@Override
	public Study create(Study study) {
		log.debug("Into create on Application Domain");
		return studyPersintence.save(study);
	}

	@Override
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null) {
			if (study.getPerson() != null)
				study.getPerson().setIdentification(personIdentification);
			if (study.getProfession() != null)
				study.getProfession().setIdentification(professionIdentification);
			return studyPersintence.save(study);
		}
		throw new NoExistException("The study of person " + personIdentification + " and profession "
				+ professionIdentification + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null)
			return studyPersintence.delete(personIdentification, professionIdentification);
		throw new NoExistException("The study of person " + personIdentification + " and profession "
				+ professionIdentification + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersintence.getClass());
		return studyPersintence.find();
	}

	@Override
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null)
			return oldStudy;
		throw new NoExistException("The study of person " + personIdentification + " and profession "
				+ professionIdentification + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}
}
