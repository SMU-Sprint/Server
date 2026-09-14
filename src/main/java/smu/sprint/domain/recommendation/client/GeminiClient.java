package smu.sprint.domain.recommendation.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import smu.sprint.domain.recommendation.client.dto.GeminiGenerateRequest;
import smu.sprint.domain.recommendation.client.dto.GeminiGenerateResponse;
import smu.sprint.domain.recommendation.client.dto.GeminiRecommendationResult;
import smu.sprint.global.code.RecommendationErrorCode;
import smu.sprint.global.exception.RecommendationException;
// Spring Boot 4.x부터 Jackson 3(tools.jackson.*)이 기본이라, 별도 ObjectMapper 빈 등록 없이
// 자동 등록되는 이 버전을 사용한다. com.fasterxml.jackson.*(Jackson 2)은 빈이 없어 주입 실패함.
// Jackson 3부터 JsonProcessingException은 사라지고 JacksonException(unchecked)으로 통합됨.
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeminiClient {

    // 최초 시도 1회 + 재시도 1회
    private static final int MAX_ATTEMPTS = 2;

    private static final Map<String, Object> RESPONSE_SCHEMA = buildResponseSchema();

    private final RestClient restClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper;
    private final GeminiPromptBuilder promptBuilder;

    public GeminiClient(@Value("${gemini.api-key}") String apiKey,
                         @Value("${gemini.model}") String model,
                         @Value("${gemini.base-url}") String baseUrl,
                         @Value("${gemini.connect-timeout-ms}") long connectTimeoutMs,
                         @Value("${gemini.read-timeout-ms}") long readTimeoutMs,
                         ObjectMapper objectMapper,
                         GeminiPromptBuilder promptBuilder) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                        .build());
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public GeminiRecommendationResult generateRecommendations(RecommendationContext context) {
        String prompt = promptBuilder.build(context);
        GeminiGenerateRequest request = new GeminiGenerateRequest(
                List.of(new GeminiGenerateRequest.Content(List.of(new GeminiGenerateRequest.Part(prompt)))),
                new GeminiGenerateRequest.GenerationConfig("application/json", RESPONSE_SCHEMA)
        );

        GeminiGenerateResponse response = callWithRetry(request);
        return parseResult(response);
    }

    private GeminiGenerateResponse callWithRetry(GeminiGenerateRequest request) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return restClient.post()
                        .uri("/models/{model}:generateContent", model)
                        .header("x-goog-api-key", apiKey)
                        .body(request)
                        .retrieve()
                        .body(GeminiGenerateResponse.class);
            } catch (RestClientException e) {
                log.warn("[ GeminiClient ]: Gemini 호출 실패 (attempt {}/{})", attempt, MAX_ATTEMPTS, e);
            }
        }
        throw new RecommendationException(RecommendationErrorCode.AI_CALL_FAILED);
    }

    private GeminiRecommendationResult parseResult(GeminiGenerateResponse response) {
        try {
            String text = response.candidates().get(0).content().parts().get(0).text();
            GeminiRecommendationResult result = objectMapper.readValue(text, GeminiRecommendationResult.class);
            validate(result);
            return result;
        } catch (Exception e) {
            log.error("[ GeminiClient ]: Gemini 응답 파싱 실패", e);
            throw new RecommendationException(RecommendationErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private void validate(GeminiRecommendationResult result) {
        List<GeminiRecommendationResult.Item> items = result.recommendations();
        if (items == null || items.size() != 3) {
            throw new IllegalStateException("추천 개수가 3개가 아닙니다.");
        }
        Set<Integer> ranks = items.stream()
                .map(GeminiRecommendationResult.Item::rank)
                .collect(Collectors.toSet());
        if (!ranks.equals(Set.of(1, 2, 3))) {
            throw new IllegalStateException("rank가 1~3이 아닙니다.");
        }
        for (GeminiRecommendationResult.Item item : items) {
            if (item.exerciseName() == null || item.exerciseName().isBlank()
                    || item.reason() == null || item.reason().isBlank()) {
                throw new IllegalStateException("필수 필드가 비어있습니다.");
            }
        }
    }

    private static Map<String, Object> buildResponseSchema() {
        String schemaJson = """
                {
                  "type": "OBJECT",
                  "properties": {
                    "recommendations": {
                      "type": "ARRAY",
                      "items": {
                        "type": "OBJECT",
                        "properties": {
                          "rank": {"type": "INTEGER"},
                          "exerciseName": {"type": "STRING"},
                          "reason": {"type": "STRING"},
                          "improvements": {"type": "STRING", "nullable": true}
                        },
                        "required": ["rank", "exerciseName", "reason"]
                      }
                    }
                  },
                  "required": ["recommendations"]
                }
                """;
        try {
            return new ObjectMapper().readValue(schemaJson, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new IllegalStateException("Gemini responseSchema 초기화 실패", e);
        }
    }
}
