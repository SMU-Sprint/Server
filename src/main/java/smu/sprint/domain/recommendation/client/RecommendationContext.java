package smu.sprint.domain.recommendation.client;

import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.recommendation.entity.RecommendedExercise;
import smu.sprint.domain.survey.entity.Survey;
import smu.sprint.domain.survey.entity.SurveyConstraint;

import java.util.List;

// 운동 추천을 생성에 필요한 정보를 모은 파라미터 객체
public record RecommendationContext(
        Member member,
        Survey survey,
        List<SurveyConstraint> constraints,
        Survey previousSurvey,
        List<SurveyConstraint> previousConstraints,
        List<RecommendedExercise> previousRecommendations
) {
    public boolean hasPreviousContext() {
        return previousSurvey != null && !previousRecommendations.isEmpty();
    }
}
