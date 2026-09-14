package smu.sprint.domain.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.sprint.domain.recommendation.entity.RecommendedExercise;
import smu.sprint.domain.recommendation.entity.RecommendedExerciseId;

import java.util.List;

public interface RecommendedExerciseRepository extends JpaRepository<RecommendedExercise, RecommendedExerciseId> {

    List<RecommendedExercise> findByIdSurveyIdOrderByIdRankAsc(Long surveyId);
}
