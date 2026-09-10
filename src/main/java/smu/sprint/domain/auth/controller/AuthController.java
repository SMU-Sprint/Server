package smu.sprint.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.sprint.domain.auth.dto.LoginRequest;
import smu.sprint.domain.auth.dto.LoginResponse;
import smu.sprint.domain.auth.service.AuthService;
import smu.sprint.global.response.CustomResponse;

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

}
