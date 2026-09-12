package smu.sprint.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.survey.entity.SurveyConstraint;
import smu.sprint.domain.survey.entity.SurveyConstraintId;

import java.util.List;

public interface SurveyConstraintRepository extends JpaRepository<SurveyConstraint, SurveyConstraintId> {

    List<SurveyConstraint> findByIdSurveyId(Long surveyId);
}
