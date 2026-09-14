package smu.sprint.domain.recommendation.client.dto;

import java.util.List;

// 실제 내용이자 Gemini가 만들어낸 추천 결과를 담는 DTO
public record GeminiRecommendationResult(
        List<Item> recommendations
) {
    public record Item(Integer rank, String exerciseName, String reason, String improvements) {
    }
}
