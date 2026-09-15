package smu.sprint.domain.facility.client.dto;

import java.util.List;

public record KakaoAddressSearchResponse(
        List<Document> documents
) {
    public record Document(
            String x, // 경도(longitude)
            String y  // 위도(latitude)
    ) {
    }
}
