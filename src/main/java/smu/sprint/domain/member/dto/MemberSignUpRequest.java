package smu.sprint.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberSignUpRequest(

        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 모두 포함 8자 이상)", example = "abcd123!")
        @NotBlank
        // 영문, 숫자, 특수문자를 모두 포함한 8자 이상
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,64}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8자 이상이어야 합니다."
        )
        String password,

        @Schema(description = "이메일로 전송된 인증 코드", example = "123456")
        @NotBlank
        String code

) {
}
