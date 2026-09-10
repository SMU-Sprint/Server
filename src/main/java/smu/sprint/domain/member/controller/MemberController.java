package smu.sprint.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.member.dto.MemberSignUpRequest;
import smu.sprint.domain.member.dto.MemberSignUpResponse;
import smu.sprint.domain.member.dto.PasswordChangeRequest;
import smu.sprint.domain.member.service.MemberService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "이메일/비밀번호/인증 코드로 회원가입을 진행합니다. " +
                    "사전에 /api/v1/mail/verification으로 발급받은 인증 코드가 필요합니다. " +
                    "성공 시 즉시 로그인 처리되어 AccessToken/RefreshToken이 함께 발급됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 / 인증 코드가 만료됨 / 인증 코드 불일치"),
            @ApiResponse(responseCode = "404", description = "발급된 인증 코드가 없음"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    @PostMapping
    public CustomResponse<MemberSignUpResponse> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        return CustomResponse.onSuccess(memberService.signUp(request));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "사전에 /api/v1/mail/verification/password-change로 발급받은 인증 코드가 필요합니다. " +
                    "변경 후에도 로그인 상태는 유지됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 / 새 비밀번호 확인 불일치 / " +
                    "인증 코드 만료 / 인증 코드 불일치 / 기존 비밀번호와 동일"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "발급된 인증 코드가 없음 / 회원을 찾을 수 없음")
    })
    @PatchMapping("/password")
    public CustomResponse<Void> changePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @Valid @RequestBody PasswordChangeRequest request) {
        memberService.changePassword(customUserDetails, request);
        return CustomResponse.onSuccess(null);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. Soft delete로 처리되며, " +
                    "탈퇴한 이메일로도 다시 회원가입할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "AccessToken에 해당하는 회원을 찾을 수 없음")
    })
    @DeleteMapping("/me")
    public CustomResponse<Void> withdraw(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.withdraw(customUserDetails);
        return CustomResponse.onSuccess(null);
    }

}
