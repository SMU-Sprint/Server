package smu.sprint.domain.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.recommendation.dto.RecommendationResponse;
import smu.sprint.domain.recommendation.dto.RecommendationResult;
import smu.sprint.domain.recommendation.service.RecommendationService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

@Tag(name = "Recommendation", description = "추천 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class RecommendationController {

    private static final String CACHED_MESSAGE =
            "이미 생성된 추천 결과입니다. 새로운 추천을 받으려면 설문을 다시 작성해주세요.";

    private final RecommendationService recommendService;

    @Operation(
            summary = "운동 추천 생성",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "회원이 가장 최근에 작성한 설문 데이터와 회원 프로필을 바탕으로 Gemini API에 운동 추천을 요청하고, " +
                    "결과를 저장 후 반환합니다. 이미 해당 설문에 대해 추천을 생성한 적이 있다면 AI를 다시 호출하지 않고 " +
                    "기존에 저장된 결과를 그대로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 생성/조회 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "작성된 설문이 없음"),
            @ApiResponse(responseCode = "412", description = "회원 프로필(성별/연령) 정보 없음"),
            @ApiResponse(responseCode = "500", description = "AI 응답 처리 중 오류"),
            @ApiResponse(responseCode = "502", description = "AI 추천 생성 호출 실패")
    })
    @PostMapping("/recommendation")
    public CustomResponse<RecommendationResponse> recommend(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        RecommendationResult result = recommendService.recommend(customUserDetails);
        return result.cached() ? CustomResponse.onSuccess(result.response(), CACHED_MESSAGE) : CustomResponse.onSuccess(result.response());
    }

    @Operation(
            summary = "운동 추천 조회",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "회원이 가장 최근에 작성한 설문에 대해 이미 생성된 추천 결과를 조회합니다. " +
                    "AI를 호출하지 않고 저장된 결과만 반환하며, 최신 설문에 대한 추천이 아직 없으면 404를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 조회 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "작성된 설문이 없거나, 최신 설문에 대한 추천 결과가 아직 없음")
    })
    @GetMapping("/recommendation")
    public CustomResponse<RecommendationResponse> getRecommendation(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        RecommendationResponse response = recommendService.getRecommendation(customUserDetails);
        return CustomResponse.onSuccess(response);
    }
}
