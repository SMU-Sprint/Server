package smu.sprint.domain.recommendation.client.dto;

import java.util.List;

// Gemini가 돌려주는 응답 봉투
public record GeminiGenerateResponse(
        List<Candidate> candidates
) {
    public record Candidate(Content content) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }
}
