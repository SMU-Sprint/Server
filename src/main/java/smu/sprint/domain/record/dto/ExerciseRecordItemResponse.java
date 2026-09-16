package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.record.entity.ExerciseRecord;

public record ExerciseRecordItemResponse(

        @Schema(description = "운동 이름")
        String exerciseName,

        @Schema(description = "운동 시간(분)")
        int durationMinutes

) {
    public static ExerciseRecordItemResponse from(ExerciseRecord record) {
        return new ExerciseRecordItemResponse(record.getExerciseName(), record.getDurationMinutes());
    }
}
