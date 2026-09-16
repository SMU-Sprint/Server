package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.record.entity.ExerciseRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExerciseRecordCreateResponse(

        @Schema(description = "운동 기록 ID")
        Long recordId,

        @Schema(description = "운동을 수행한 날짜")
        LocalDate exerciseDate,

        @Schema(description = "운동 이름")
        String exerciseName,

        @Schema(description = "운동 시간(분)")
        int durationMinutes,

        @Schema(description = "기록 등록 시간")
        LocalDateTime createdAt

) {
    public static ExerciseRecordCreateResponse from(ExerciseRecord record) {
        return new ExerciseRecordCreateResponse(
                record.getRecordId(),
                record.getExerciseDate(),
                record.getExerciseName(),
                record.getDurationMinutes(),
                record.getCreatedAt()
        );
    }
}
