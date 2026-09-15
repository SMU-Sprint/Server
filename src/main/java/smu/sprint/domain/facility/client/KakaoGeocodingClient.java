package smu.sprint.domain.facility.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import smu.sprint.domain.facility.client.dto.KakaoAddressSearchResponse;

import java.util.List;
import java.util.Optional;

// 카카오 로컬 API로 주소 문자열을 좌표로 변환한다. facility 백필 배치(FacilityGeocodingRunner)에서만 사용.
@Slf4j
@Component
public class KakaoGeocodingClient {

    // 최초 시도 1회 + 재시도 1회 (GeminiClient와 동일한 정책)
    private static final int MAX_ATTEMPTS = 2;

    // 주소 검색과 키워드 검색 호출 사이 최소 간격(ms)
    private static final long FALLBACK_DELAY_MS = 150;

    private final RestClient restClient;
    private final String apiKey;

    public KakaoGeocodingClient(@Value("${kakao.api-key}") String apiKey,
                                 @Value("${kakao.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // 주소로 좌표를 조회한다. 도로명/지번 주소 검색(/address.json)은 매칭이 엄격해서 실패율이 높아,
    // 실패하면 지도 검색처럼 느슨하게 매칭하는 키워드 검색(/keyword.json)으로 한 번 더 시도하고,
    // 그래도 실패하면 마지막으로 시설명으로 키워드 검색한다.
    // 주의: 시설명이 흔한 일반명사에 가까우면 전국의 동명 시설 중 엉뚱한 곳이 1순위로 잡혀
    // "못 찾음"이 아니라 "틀린 좌표"가 들어갈 수 있다 (호출 측에서 감수하기로 한 트레이드오프).
    // 응답 스키마(documents[].x/y)가 동일해 같은 DTO(KakaoAddressSearchResponse)를 재사용한다.
    public Optional<Coordinate> geocode(String address, String name) throws InterruptedException {
        Optional<Coordinate> byAddress = search("/v2/local/search/address.json", address);
        if (byAddress.isPresent()) {
            return byAddress;
        }

        Thread.sleep(FALLBACK_DELAY_MS);
        Optional<Coordinate> byAddressKeyword = search("/v2/local/search/keyword.json", address);
        if (byAddressKeyword.isPresent()) {
            return byAddressKeyword;
        }

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        Thread.sleep(FALLBACK_DELAY_MS);
        return search("/v2/local/search/keyword.json", name);
    }

    private Optional<Coordinate> search(String path, String query) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                KakaoAddressSearchResponse response = restClient.get()
                        .uri(uriBuilder -> uriBuilder.path(path)
                                .queryParam("query", query)
                                .build())
                        .header("Authorization", "KakaoAK " + apiKey)
                        .retrieve()
                        .body(KakaoAddressSearchResponse.class);

                return extractCoordinate(response);
            } catch (RestClientException e) {
                log.warn("[ KakaoGeocodingClient ]: 지오코딩 호출 실패 (attempt {}/{}) path='{}' query='{}'", attempt, MAX_ATTEMPTS, path, query, e);
            }
        }
        return Optional.empty();
    }

    private Optional<Coordinate> extractCoordinate(KakaoAddressSearchResponse response) {
        List<KakaoAddressSearchResponse.Document> documents = response == null ? null : response.documents();
        if (documents == null || documents.isEmpty()) {
            return Optional.empty();
        }
        // 카카오 API는 x=경도, y=위도로 내려준다 (GeoJSON 좌표 순서 관례와 동일, 헷갈리기 쉬운 부분).
        KakaoAddressSearchResponse.Document first = documents.get(0);
        return Optional.of(new Coordinate(Double.parseDouble(first.y()), Double.parseDouble(first.x())));
    }

    public record Coordinate(double latitude, double longitude) {
    }

}
