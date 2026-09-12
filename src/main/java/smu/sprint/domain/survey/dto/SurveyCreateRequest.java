package smu.sprint.domain.survey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import smu.sprint.domain.survey.entity.ConstraintType;
import smu.sprint.domain.survey.entity.ExercisePurpose;
import smu.sprint.domain.survey.entity.ExerciseSpot;
import smu.sprint.domain.survey.entity.OccupationType;

import java.time.LocalTime;
import java.util.List;

public record SurveyCreateRequest(

        @Schema(description = "운동 목적", example = "체형관리")
        @NotNull
        ExercisePurpose exercisePurpose,

        @Schema(description = "운동 목적 '기타' 선택 시 상세 내용", example = "재활 목적")
        String exercisePurposeEtc,

        @Schema(description = "운동 경험 여부", example = "true")
        @NotNull
        Boolean exerciseExperienceFlag,

        @Schema(description = "운동 경험 상세 (경험이 있는 경우)", example = "헬스 6개월")
        String exerciseExperienceDetail,

        @Schema(description = "선호 스포츠", example = "수영")
        @Size(max = 50)
        String preferredSport,

        @Schema(description = "직업 특성", example = "비활동")
        @NotNull
        OccupationType occupationType,

        @Schema(description = "지난 일주일간 숨가쁘게 운동한 일수 (0~7)", example = "2")
        @NotNull(message = "0~7 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~7 사이의 값이어야 합니다.")
        @Max(value = 7, message = "0~7 사이의 값이어야 합니다.")
        Integer vigorousDays,

        @Schema(description = "숨가쁜 운동 1회 시간(분) (0~420)", example = "30")
        @NotNull(message = "0~420 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~420 사이의 값이어야 합니다.")
        @Max(value = 420, message = "0~420 사이의 값이어야 합니다.")
        Integer vigorousDurationMinutes,

        @Schema(description = "지난 일주일간 가벼운 운동을 한 일수 (0~7)", example = "3")
        @NotNull(message = "0~7 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~7 사이의 값이어야 합니다.")
        @Max(value = 7, message = "0~7 사이의 값이어야 합니다.")
        Integer moderateDays,

        @Schema(description = "가벼운 운동 1회 시간(분) (0~420)", example = "40")
        @NotNull(message = "0~420 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~420 사이의 값이어야 합니다.")
        @Max(value = 420, message = "0~420 사이의 값이어야 합니다.")
        Integer moderateDurationMinutes,

        @Schema(description = "지난 일주일간 20분 이상 걸은 일수 (0~7)", example = "5")
        @NotNull(message = "0~7 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~7 사이의 값이어야 합니다.")
        @Max(value = 7, message = "0~7 사이의 값이어야 합니다.")
        Integer walkingDays,

        @Schema(description = "20분 이상 걷기 1회 시간(분) (0~420)", example = "20")
        @NotNull(message = "0~420 사이의 값이어야 합니다.")
        @Min(value = 0, message = "0~420 사이의 값이어야 합니다.")
        @Max(value = 420, message = "0~420 사이의 값이어야 합니다.")
        Integer walkingDurationMinutes,

        @Schema(description = "업무 시작 시간", example = "09:00:00")
        LocalTime workStartTime,

        @Schema(description = "업무 종료 시간", example = "18:00:00")
        LocalTime workEndTime,

        @Schema(description = "주요 운동 장소", example = "산책로")
        @NotNull
        ExerciseSpot exerciseSpot,

        @Schema(description = "운동 장소 '기타' 선택 시 상세 내용", example = "헬스장")
        String exerciseSpotEtc,

        @Schema(description = "지난 한 달간 피곤했는지 여부", example = "false")
        @NotNull
        Boolean fatigueFlag,

        @Schema(description = "쉬지 않고 계단 10개를 오를 수 있는지 여부", example = "true")
        @NotNull
        Boolean stairClimbFlag,

        @Schema(description = "쉬지 않고 300m 이동 가능한지 여부", example = "true")
        @NotNull
        Boolean walk300mFlag,

        @Schema(description = "최근 1년간 체중이 5% 이상 감소했는지 여부", example = "false")
        @NotNull
        Boolean weightLossFlag,

        @Schema(description = "운동 지속의 어려운 점 (다중 선택)", example = "[\"시간부족\", \"경제적비용\"]")
        @NotEmpty
        List<ConstraintType> constraintTypes,

        @Schema(description = "제약사항 '기타' 선택 시 상세 내용", example = "회사 특근이 잦아 시간 내기 어려움")
        String constraintEtc

) {
}