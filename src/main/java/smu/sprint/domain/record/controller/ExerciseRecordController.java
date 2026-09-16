package smu.sprint.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import smu.sprint.domain.record.dto.ExerciseHeatmapResponse;
import smu.sprint.domain.record.dto.ExerciseRecordCreateRequest;
import smu.sprint.domain.record.dto.ExerciseRecordCreateResponse;
import smu.sprint.domain.record.service.ExerciseRecordService;
import smu.sprint.global.code.GeneralErrorCode;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

import java.time.LocalDate;

@Slf4j
@Tag(name = "ExerciseRecord", description = "운동 기록 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/exercise-records")
@Validated
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    @Operation(
            summary = "운동 기록 등록",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "특정 날짜에 수행한 운동(이름, 시간)을 기록합니다. 하루에 여러 건 등록할 수 있으며, " +
                    "과거 날짜로도 등록할 수 있으나 미래 날짜는 등록할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운동 기록 등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (미래 날짜, 빈 운동 이름, 잘못된 시간 등)"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨")
    })
    @PostMapping
    public CustomResponse<ExerciseRecordCreateResponse> createRecord(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ExerciseRecordCreateRequest request) {
        ExerciseRecordCreateResponse response = exerciseRecordService.createRecord(customUserDetails, request);
        return CustomResponse.onSuccess(response);
    }

    @Operation(
            summary = "운동 기록 잔디(히트맵) 조회",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "endDate를 기준으로 최근 3개월(endDate 포함) 구간의 날짜별 운동 기록 개수와 진하기 단계(0~4)를 반환합니다. " +
                    "endDate를 생략하면 오늘 날짜를 기준으로 조회하며, 기록이 없는 날짜도 count=0으로 포함해 모두 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "잔디 조회 성공"),
            @ApiResponse(responseCode = "400", description = "endDate 형식이 올바르지 않거나 미래 날짜임"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨")
    })
    @GetMapping("/heatmap")
    public CustomResponse<ExerciseHeatmapResponse> getHeatmap(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @PastOrPresent(message = "미래 날짜는 조회할 수 없습니다.")
            LocalDate endDate) {
        ExerciseHeatmapResponse response = exerciseRecordService.getHeatmap(customUserDetails, endDate);
        return CustomResponse.onSuccess(response);
    }

    // endDate=abc 처럼 파싱 자체가 불가능한 경우, Spring이 컨트롤러 메서드 진입 전에 던지는 예외.
    // 전역 GlobalExceptionHandler에는 해당 예외 처리기가 없어 그대로 두면 500으로 새므로, 이 컨트롤러 범위에서만 400으로 매핑한다.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<CustomResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("[ MethodArgumentTypeMismatchException ]: '{}' 파라미터 형식이 올바르지 않습니다.", ex.getName());
        return ResponseEntity.status(GeneralErrorCode.BAD_REQUEST_400.getHttpStatus())
                .body(CustomResponse.onFailure(GeneralErrorCode.BAD_REQUEST_400.getCode(), GeneralErrorCode.BAD_REQUEST_400.getMessage()));
    }

}
