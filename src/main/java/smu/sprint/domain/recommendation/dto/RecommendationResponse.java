package smu.sprint.domain.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.recommendation.entity.RecommendedExercise;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record RecommendationResponse(

        @Schema(description = "설문 ID")
        Long surveyId,

        @Schema(description = "추천 운동 목록")
        List<RecommendationItem> recommendations,

        @Schema(description = "추천 생성 시간")
        LocalDateTime createdAt

) {
    public static RecommendationResponse from(Long surveyId, List<RecommendedExercise> saved) {
        List<RecommendationItem> items = saved.stream()
                .sorted(Comparator.comparing(exercise -> exercise.getId().getRank()))
                .map(exercise -> new RecommendationItem(
                        exercise.getId().getRank(),
                        exercise.getExerciseName(),
                        exercise.getReason(),
                        exercise.getImprovements()
                ))
                .toList();

        LocalDateTime createdAt = saved.stream()
                .map(RecommendedExercise::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new RecommendationResponse(surveyId, items, createdAt);
    }

    public record RecommendationItem(

            @Schema(description = "추천 순위")
            Integer rank,

            @Schema(description = "운동명")
            String exerciseName,

            @Schema(description = "추천 이유")
            String reason,

            @Schema(description = "이전 대비 개선된 점 (이전 설문 이력이 있을 때만 값이 채워짐)")
            String improvements

    ) {
    }
}
