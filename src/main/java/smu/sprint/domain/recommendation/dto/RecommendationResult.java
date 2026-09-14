package smu.sprint.domain.recommendation.dto;

public record RecommendationResult(
        RecommendationResponse response,
        boolean cached
        // cached : 이미 저장된 결과인지, 새롭게 AI를 통해서 만든 건지
        // controller 에서 해당 값을 보고 상황에 맞는 문구와 결과를 제공
) {
}
