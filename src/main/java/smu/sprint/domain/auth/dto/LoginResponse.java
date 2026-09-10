package smu.sprint.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.global.security.jwt.JwtDTO;

public record LoginResponse(
        @Schema(description = "로그인한 이메일", example = "user@example.com")
        String email,

        @Schema(description = "발급된 AccessToken/RefreshToken")
        JwtDTO token
) {
}
