package smu.sprint.domain.facility.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.facility.repository.FacilityNearbyProjection;

import java.util.List;

public record FacilitySearchResponse(

        @Schema(description = "조회된 시설 목록 (반경 내 결과가 없으면 빈 배열)")
        List<FacilityItem> facilities

) {
    public static FacilitySearchResponse from(List<FacilityNearbyProjection> rows) {
        List<FacilityItem> facilities = rows.stream()
                .map(FacilityItem::from)
                .toList();
        return new FacilitySearchResponse(facilities);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FacilityItem(

            @Schema(description = "시설 ID")
            Long facilityId,

            @Schema(description = "시설명")
            String name,

            @Schema(description = "종목")
            String type,

            @Schema(description = "종목에 매핑된 운동명 (여러 개면 ', '로 구분, 매핑이 없으면 응답에서 키가 생략됨)")
            String exerciseName,

            @Schema(description = "주소")
            String address,

            @Schema(description = "위도")
            Double latitude,

            @Schema(description = "경도")
            Double longitude,

            @Schema(description = "기준 좌표로부터의 거리(km)")
            Double distanceKm,

            @Schema(description = "개방 시작 시간 (원본 데이터에 없으면 응답에서 키가 생략됨)")
            String openTime,

            @Schema(description = "개방 종료 시간 (원본 데이터에 없으면 응답에서 키가 생략됨)")
            String closeTime

    ) {
        public static FacilityItem from(FacilityNearbyProjection row) {
            return new FacilityItem(
                    row.getFacilityId(),
                    row.getName(),
                    row.getType(),
                    row.getExerciseName(),
                    row.getAddress(),
                    row.getLatitude(),
                    row.getLongitude(),
                    row.getDistanceKm(),
                    row.getOpenTime(),
                    row.getCloseTime()
            );
        }
    }
}
