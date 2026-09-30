package smu.sprint.domain.mail.controller;

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
import smu.sprint.domain.mail.dto.EmailVerificationConfirmRequest;
import smu.sprint.domain.mail.dto.EmailVerificationConfirmResponse;
import smu.sprint.domain.mail.dto.EmailVerificationRequest;
import smu.sprint.domain.mail.service.EmailVerificationService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

@Tag(name = "Mail", description = "이메일 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "회원가입 이메일 인증 코드 발급",
            description = "입력한 이메일로 인증 코드를 발송합니다. 이미 가입된 이메일이면 발급되지 않습니다. " +
                    "코드는 10분간 유효하며, 재발급은 60초에 한 번만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일"),
            @ApiResponse(responseCode = "429", description = "인증 코드 재요청이 너무 잦음")
    })
    @PostMapping("/verification")
    public CustomResponse<Void> issueSignUpCode(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.issueSignUpCode(request);
        return CustomResponse.onSuccess(null);
    }

    @Operation(
            summary = "비밀번호 변경 이메일 인증 코드 발급",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "현재 로그인된 계정의 이메일로 인증 코드를 발송합니다. " +
                    "코드는 10분간 유효하며, 재발급은 60초에 한 번만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "429", description = "인증 코드 재요청이 너무 잦음")
    })
    @PostMapping("/verification/password-change")
    public CustomResponse<Void> issuePasswordChangeCode(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        emailVerificationService.issuePasswordChangeCode(customUserDetails.getUsername());
        return CustomResponse.onSuccess(null);
    }

    @Operation(
            summary = "비밀번호 찾기 이메일 인증 코드 발급",
            description = "가입된 회원의 이메일로 인증 코드를 발송합니다. " +
                    "코드는 10분간 유효하며, 재발급은 60초에 한 번만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "가입되지 않은 이메일"),
            @ApiResponse(responseCode = "429", description = "인증 코드 재요청이 너무 잦음")
    })
    @PostMapping("/verification/find-password")
    public CustomResponse<Void> issueFindPasswordCode(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.issueFindPasswordCode(request.email());
        return CustomResponse.onSuccess(null);
    }

    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "발급받은 인증 코드를 검증하고, 검증 토큰을 발급합니다. " +
                    "발급받은 토큰은 코드를 발급받았던 것과 동일한 목적(purpose)의 API(회원가입 / 비밀번호 변경 / 비밀번호 찾기)에서만 사용할 수 있으며, " +
                    "5분간 유효하고 1회 사용 후 만료됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 검증 성공, 검증 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 / 인증 코드가 만료됨 / 인증 코드 불일치"),
            @ApiResponse(responseCode = "404", description = "발급된 인증 코드가 없음")
    })
    @PostMapping("/verification/confirm")
    public CustomResponse<EmailVerificationConfirmResponse> confirmCode(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        String token = emailVerificationService.confirmCode(request.email(), request.code(), request.purpose());
        return CustomResponse.onSuccess(new EmailVerificationConfirmResponse(token));
    }

}
