package smu.sprint.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.survey.entity.Survey;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}
