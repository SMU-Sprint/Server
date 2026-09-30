package smu.sprint.domain.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmailVerificationConfirmResponse(

        @Schema(description = "검증 토큰. 발급받은 목적의 API 호출 시 인증 코드 대신 사용합니다.", example = "3f9a1c2e...")
        String token

) {
}
