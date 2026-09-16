package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExerciseRecordCreateRequest(

        @Schema(description = "운동을 수행한 날짜 (오늘 이전 또는 오늘)", example = "2026-09-16")
        @NotNull
        @PastOrPresent(message = "미래 날짜는 등록할 수 없습니다.")
        LocalDate exerciseDate,

        @Schema(description = "운동 이름", example = "러닝")
        @NotBlank
        @Size(max = 100)
        String exerciseName,

        @Schema(description = "운동 시간(분)", example = "30")
        @NotNull
        @Positive(message = "1분 이상이어야 합니다.")
        @Max(value = 1440, message = "하루 최대 시간(1440분)을 초과할 수 없습니다.")
        Integer durationMinutes

) {
}
