package smu.sprint.domain.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record ExerciseHeatmapResponse(

        @Schema(description = "조회 구간 시작일")
        LocalDate startDate,

        @Schema(description = "조회 구간 종료일")
        LocalDate endDate,

        @Schema(description = "구간 내 모든 날짜의 운동 기록 개수/단계 (기록 없는 날짜도 count=0으로 포함)")
        List<ExerciseHeatmapDayResponse> days

) {
}
