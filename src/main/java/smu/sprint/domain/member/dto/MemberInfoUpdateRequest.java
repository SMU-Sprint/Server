package smu.sprint.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import smu.sprint.domain.member.entity.Gender;

public record MemberInfoUpdateRequest(

        @Schema(description = "이메일로 전송된 인증 코드", example = "123456")
        @NotBlank
        String code,

        @Schema(description = "이름", example = "홍길동")
        @NotBlank
         @Size(max = 50)
        String name,

        @Schema(description = "신장(cm)", example = "170.5")
        @NotNull
        @Positive
        @DecimalMax("250.0")
        Double height,

        @Schema(description = "몸무게(kg)", example = "65.0")
        @NotNull
        @Positive
        @DecimalMax("300.0")
        Double weight,

        @Schema(description = "성별")
        @NotNull
        Gender gender,

        @Schema(description = "나이", example = "25")
        @NotNull
        @Min(0)
        @Max(150)
        Integer age

) {
}
