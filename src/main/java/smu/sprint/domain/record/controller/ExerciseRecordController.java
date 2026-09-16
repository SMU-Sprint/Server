package smu.sprint.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.record.dto.ExerciseRecordCreateRequest;
import smu.sprint.domain.record.dto.ExerciseRecordCreateResponse;
import smu.sprint.domain.record.service.ExerciseRecordService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

@Tag(name = "ExerciseRecord", description = "운동 기록 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/exercise-records")
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

}
