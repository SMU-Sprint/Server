package smu.sprint.domain.facility.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import smu.sprint.domain.facility.dto.FacilitySearchResponse;
import smu.sprint.domain.facility.service.FacilityService;
import smu.sprint.global.code.FacilityErrorCode;
import smu.sprint.global.response.CustomResponse;

@Slf4j
@Tag(name = "Facility", description = "시설 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/facilities")
@Validated
public class FacilityController {

    private final FacilityService facilityService;

    @Operation(
            summary = "주변 체육시설 조회",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "위도/경도를 기준으로 반경(radiusKm, 기본 3km, 최대 20km) 내 체육시설을 거리순으로 조회합니다. " +
                    "exerciseName을 지정하면 매핑된 시설 종류로 필터링하며, 매핑이 없거나 생략하면 필터 없이 전체를 반환합니다. " +
                    "검색 결과가 없어도 200과 빈 배열을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시설 조회 성공 (결과 없음 포함)"),
            @ApiResponse(responseCode = "400", description = "latitude/longitude 누락·형식 오류이거나 radiusKm/좌표 범위를 벗어남"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨")
    })
    @GetMapping
    public CustomResponse<FacilitySearchResponse> searchFacilities(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "3") @Positive @DecimalMax("20.0") Double radiusKm,
            @RequestParam(required = false) String exerciseName) {
        FacilitySearchResponse response = facilityService.searchNearbyFacilities(latitude, longitude, radiusKm, exerciseName);
        return CustomResponse.onSuccess(response);
    }

    // latitude=abc 처럼 파싱 자체가 불가능한 경우, Spring이 컨트롤러 메서드 진입 전에 던지는 예외.
    // 전역 GlobalExceptionHandler에는 해당 예외 처리기가 없어 그대로 두면 500으로 새므로, 이 컨트롤러 범위에서만 400으로 매핑한다.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<CustomResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("[ MethodArgumentTypeMismatchException ]: '{}' 파라미터 형식이 올바르지 않습니다.", ex.getName());
        FacilityErrorCode errorCode = FacilityErrorCode.INVALID_COORDINATE;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(CustomResponse.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }
}
