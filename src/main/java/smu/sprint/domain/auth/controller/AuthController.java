package smu.sprint.domain.auth.controller;

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
import smu.sprint.domain.auth.dto.LoginRequest;
import smu.sprint.domain.auth.dto.LoginResponse;
import smu.sprint.domain.auth.dto.ReissueRequest;
import smu.sprint.domain.auth.service.AuthService;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.jwt.JwtDTO;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = "이메일/비밀번호로 로그인합니다. 성공 시 AccessToken/RefreshToken이 함께 발급됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public CustomResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return CustomResponse.onSuccess(authService.login(request));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "RefreshToken을 검증하여 AccessToken과 RefreshToken을 함께 재발급합니다. " +
                    "재발급할 때마다 RefreshToken도 새로 갱신되어 만료 기한이 연장됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 RefreshToken"),
            @ApiResponse(responseCode = "404", description = "RefreshToken에 해당하는 회원 또는 저장된 토큰을 찾을 수 없음")
    })
    @PostMapping("/reissue")
    public CustomResponse<JwtDTO> reissue(@Valid @RequestBody ReissueRequest request) {
        return CustomResponse.onSuccess(authService.reissue(request));
    }

    @Operation(
            summary = "로그아웃",
            description = "로그인된 상태(유효한 AccessToken)에서만 호출 가능합니다. " +
                    "서버에 저장된 RefreshToken을 폐기합니다. AccessToken은 클라이언트가 즉시 폐기해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "AccessToken이 없거나 유효하지 않음/만료됨"),
            @ApiResponse(responseCode = "404", description = "AccessToken에 해당하는 회원을 찾을 수 없음")
    })
    @PostMapping("/logout")
    public CustomResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        authService.logout(customUserDetails);
        return CustomResponse.onSuccess(null);
    }

}
