package smu.sprint.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(

        @Schema(description = "재발급에 사용할 RefreshToken")
        @NotBlank
        String refreshToken

) {
}
