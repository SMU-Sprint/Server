package smu.sprint.domain.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.survey.dto.SurveyCreateRequest;
import smu.sprint.domain.survey.dto.SurveyCreateResponse;
import smu.sprint.domain.survey.service.SurveyService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

@Tag(name = "Survey", description = "설문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(
            summary = "설문지 작성(회원가입시) / 재작성(개인페이지에서)",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "회원가입 직후(자동 로그인된 토큰 사용) 신규 작성 또는 마이페이지에서 재작성(수정) 시 " +
                    "동일한 API를 호출합니다. 호출할 때마다 새로운 설문 이력을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 작성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 / '기타' 선택 시 상세 내용 누락"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨")
    })
    @PostMapping("/survey")
    public CustomResponse<SurveyCreateResponse> createSurvey(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody SurveyCreateRequest request) {
        SurveyCreateResponse response = surveyService.createSurvey(customUserDetails, request);
        return CustomResponse.onSuccess(response);
    }

    @Operation(
            summary = "설문지 조회",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "member의 개인 프로필에서 개인의 가장 최신 설문 1건만 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 조회 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "작성된 설문이 없음")
    })
    @GetMapping("/survey")
    public CustomResponse<SurveyCreateResponse> getLatestSurvey(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SurveyCreateResponse response = surveyService.getLatestSurvey(customUserDetails);
        return CustomResponse.onSuccess(response);
    }

}