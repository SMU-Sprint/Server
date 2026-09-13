package smu.sprint.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import smu.sprint.domain.member.entity.Gender;

public record MemberInfoUpdateRequest(

        @Schema(description = "이름", example = "홍길동")
        @NotBlank
        String name,

        @Schema(description = "신장(cm)", example = "170.5")
        @NotNull
        @Positive
        Double height,

        @Schema(description = "몸무게(kg)", example = "65.0")
        @NotNull
        @Positive
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
