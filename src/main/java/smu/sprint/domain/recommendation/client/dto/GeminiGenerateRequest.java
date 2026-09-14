package smu.sprint.domain.recommendation.client.dto;

import java.util.List;
import java.util.Map;

// 우리가 Gemini에게 보내는 요청 봉투
public record GeminiGenerateRequest(
        List<Content> contents,
        GenerationConfig generationConfig
) {
    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record GenerationConfig(String responseMimeType, Map<String, Object> responseSchema) {
    }
}
