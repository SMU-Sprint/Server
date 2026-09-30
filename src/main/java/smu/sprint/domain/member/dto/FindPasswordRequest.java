package smu.sprint.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindPasswordRequest(

        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "이메일 인증 완료 후 발급받은 검증 토큰", example = "3f9a1c2e...")
        @NotBlank
        String token

) {
}
