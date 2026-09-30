package smu.sprint.domain.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import smu.sprint.domain.mail.entity.VerificationPurpose;

public record EmailVerificationConfirmRequest(

        @Schema(description = "인증 코드를 발급받은 이메일", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "이메일로 전송된 인증 코드", example = "123456")
        @NotBlank
        String code,

        @Schema(description = "인증 코드를 발급받은 목적. 발급 시 사용한 API와 일치해야 합니다.", example = "SIGN_UP")
        @NotNull
        VerificationPurpose purpose

) {
}
