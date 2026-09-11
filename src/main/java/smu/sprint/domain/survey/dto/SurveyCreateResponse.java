package smu.sprint.domain.survey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import smu.sprint.domain.survey.entity.ConstraintType;
import smu.sprint.domain.survey.entity.ExercisePurpose;
import smu.sprint.domain.survey.entity.ExerciseSpot;
import smu.sprint.domain.survey.entity.OccupationType;
import smu.sprint.domain.survey.entity.Survey;
import smu.sprint.domain.survey.entity.SurveyConstraint;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record SurveyCreateResponse(

        @Schema(description = "설문 ID")
        Long surveyId,

        @Schema(description = "운동 목적")
        ExercisePurpose exercisePurpose,

        @Schema(description = "운동 목적 기타")
        String exercisePurposeEtc,

        @Schema(description = "운동 경험 여부")
        boolean exerciseExperienceFlag,

        @Schema(description = "운동 경험 상세")
        String exerciseExperienceDetail,

        @Schema(description = "선호 스포츠")
        String preferredSport,

        @Schema(description = "직업 특성")
        OccupationType occupationType,

        @Schema(description = "숨가쁜 운동 일수")
        int vigorousDays,

        @Schema(description = "숨가쁜 운동 1회 시간(분)")
        int vigorousDurationMinutes,

        @Schema(description = "가벼운 운동 일수")
        int moderateDays,

        @Schema(description = "가벼운 운동 1회 시간(분)")
        int moderateDurationMinutes,

        @Schema(description = "걷기 일수")
        int walkingDays,

        @Schema(description = "걷기 1회 시간(분)")
        int walkingDurationMinutes,

        @Schema(description = "업무 시작 시간")
        LocalTime workStartTime,

        @Schema(description = "업무 종료 시간")
        LocalTime workEndTime,

        @Schema(description = "주요 운동 장소")
        ExerciseSpot exerciseSpot,

        @Schema(description = "운동 장소 기타")
        String exerciseSpotEtc,

        @Schema(description = "피곤함 여부")
        boolean fatigueFlag,

        @Schema(description = "계단 오르기 가능 여부")
        boolean stairClimbFlag,

        @Schema(description = "300m 이동 가능 여부")
        boolean walk300mFlag,

        @Schema(description = "체중 5% 감소 여부")
        boolean weightLossFlag,

        @Schema(description = "설문 생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "운동 지속의 어려운 점 목록")
        List<ConstraintType> constraintTypes,

        @Schema(description = "제약사항 '기타' 선택 시 상세 내용")
        String constraintEtc

) {
    public static SurveyCreateResponse from(Survey survey, List<SurveyConstraint> constraints) {
        List<ConstraintType> constraintTypes = constraints.stream()
                .map(constraint -> constraint.getId().getConstraintType())
                .toList();

        String constraintEtc = constraints.stream()
                .filter(constraint -> constraint.getId().getConstraintType() == ConstraintType.기타)
                .map(SurveyConstraint::getConstraintEtc)
                .findFirst()
                .orElse(null);

        return new SurveyCreateResponse(
                survey.getSurveyId(),
                survey.getExercisePurpose(),
                survey.getExercisePurposeEtc(),
                survey.isExerciseExperienceFlag(),
                survey.getExerciseExperienceDetail(),
                survey.getPreferredSport(),
                survey.getOccupationType(),
                survey.getVigorousDays(),
                survey.getVigorousDurationMinutes(),
                survey.getModerateDays(),
                survey.getModerateDurationMinutes(),
                survey.getWalkingDays(),
                survey.getWalkingDurationMinutes(),
                survey.getWorkStartTime(),
                survey.getWorkEndTime(),
                survey.getExerciseSpot(),
                survey.getExerciseSpotEtc(),
                survey.isFatigueFlag(),
                survey.isStairClimbFlag(),
                survey.isWalk300mFlag(),
                survey.isWeightLossFlag(),
                survey.getCreatedAt(),
                constraintTypes,
                constraintEtc
        );
    }
}