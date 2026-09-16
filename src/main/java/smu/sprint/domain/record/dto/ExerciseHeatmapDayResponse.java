package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ExerciseHeatmapDayResponse(

        @Schema(description = "날짜")
        LocalDate date,

        @Schema(description = "해당 날짜의 운동 기록 개수")
        long count,

        @Schema(description = "잔디 진하기 단계 (0~4)")
        int level

) {
}
