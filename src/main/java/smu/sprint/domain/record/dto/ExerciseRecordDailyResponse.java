package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.record.entity.ExerciseRecord;

import java.time.LocalDate;
import java.util.List;

public record ExerciseRecordDailyResponse(

        @Schema(description = "조회한 날짜")
        LocalDate date,

        @Schema(description = "해당 날짜의 총 운동 시간(분)")
        int totalDurationMinutes,

        @Schema(description = "해당 날짜에 수행한 운동 목록")
        List<ExerciseRecordItemResponse> records

) {
    public static ExerciseRecordDailyResponse of(LocalDate date, List<ExerciseRecord> records) {
        int totalDurationMinutes = records.stream()
                .mapToInt(ExerciseRecord::getDurationMinutes)
                .sum();

        List<ExerciseRecordItemResponse> items = records.stream()
                .map(ExerciseRecordItemResponse::from)
                .toList();

        return new ExerciseRecordDailyResponse(date, totalDurationMinutes, items);
    }
}
