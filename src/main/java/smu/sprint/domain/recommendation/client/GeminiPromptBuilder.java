package smu.sprint.domain.recommendation.client;

import org.springframework.stereotype.Component;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.survey.entity.ConstraintType;
import smu.sprint.domain.survey.entity.Survey;
import smu.sprint.domain.survey.entity.SurveyConstraint;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeminiPromptBuilder {

    public String build(RecommendationContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 운동 처방 전문가입니다. 아래 회원 정보와 설문 응답을 바탕으로, ")
                .append("이 회원에게 적합한 운동 3가지를 추천해주세요.\n\n");

        appendMemberProfile(sb, context.member());
        appendSurveyDetail(sb, context.survey(), context.constraints());

        if (context.hasPreviousContext()) {
            appendPreviousContext(sb, context);
        } else {
            sb.append("\n이 회원은 이전 설문 이력이 없습니다. 모든 추천의 improvements 필드는 반드시 null로 두세요.\n\n");
        }

        sb.append("반드시 정확히 3개의 운동을 추천하고, rank는 1, 2, 3을 각각 한 번씩만 사용하며, ")
                .append("서로 다른 운동이어야 합니다. 각 추천에는 exerciseName(운동명), ")
                .append("reason(추천 이유, 한국어 한두 문장)을 반드시 포함하세요.");

        return sb.toString();
    }

    private void appendMemberProfile(StringBuilder sb, Member member) {
        sb.append("[회원 프로필]\n")
                .append("- 성별: ").append(member.getGender()).append("\n")
                .append("- 연령: ").append(member.getAge()).append("세\n");
        if (member.getHeight() != null) {
            sb.append("- 신장: ").append(member.getHeight()).append("cm\n");
        }
        if (member.getWeight() != null) {
            sb.append("- 몸무게: ").append(member.getWeight()).append("kg\n");
        }
        sb.append("\n");
    }

    private void appendSurveyDetail(StringBuilder sb, Survey survey, List<SurveyConstraint> constraints) {
        sb.append("[현재 설문]\n")
                .append("- 운동 목적: ").append(describeExercisePurpose(survey)).append("\n")
                .append("- 운동 경험: ").append(survey.isExerciseExperienceFlag()
                        ? "있음(" + nullToDash(survey.getExerciseExperienceDetail()) + ")" : "없음").append("\n")
                .append("- 선호 스포츠: ").append(nullToDash(survey.getPreferredSport())).append("\n")
                .append("- 직업 특성: ").append(survey.getOccupationType()).append("\n")
                .append("- 격렬한 운동: 주 ").append(survey.getVigorousDays())
                .append("일, 1회 ").append(survey.getVigorousDurationMinutes()).append("분\n")
                .append("- 중강도 운동: 주 ").append(survey.getModerateDays())
                .append("일, 1회 ").append(survey.getModerateDurationMinutes()).append("분\n")
                .append("- 걷기: 주 ").append(survey.getWalkingDays())
                .append("일, 1회 ").append(survey.getWalkingDurationMinutes()).append("분\n")
                .append("- 주요 운동 장소: ").append(describeExerciseSpot(survey)).append("\n")
                .append("- 최근 피로감: ").append(survey.isFatigueFlag() ? "있음" : "없음").append("\n")
                .append("- 쉬지 않고 계단 오르기 가능 여부: ").append(survey.isStairClimbFlag() ? "가능" : "어려움").append("\n")
                .append("- 쉬지 않고 300m 이동 가능 여부: ").append(survey.isWalk300mFlag() ? "가능" : "어려움").append("\n")
                .append("- 최근 1년 체중 5% 이상 감소 여부: ").append(survey.isWeightLossFlag() ? "예" : "아니오").append("\n")
                .append("- 운동 지속의 어려운 점: ").append(describeConstraints(constraints)).append("\n");
    }

    private void appendPreviousContext(StringBuilder sb, RecommendationContext context) {
        Survey previousSurvey = context.previousSurvey();
        sb.append("\n[이전 설문 요약]\n")
                .append("- 운동 목적: ").append(describeExercisePurpose(previousSurvey)).append("\n")
                .append("- 직업 특성: ").append(previousSurvey.getOccupationType()).append("\n")
                .append("- 활동 수준: 격렬한 운동 주 ").append(previousSurvey.getVigorousDays())
                .append("일, 중강도 운동 주 ").append(previousSurvey.getModerateDays())
                .append("일, 걷기 주 ").append(previousSurvey.getWalkingDays()).append("일\n")
                .append("- 제약사항: ").append(describeConstraints(context.previousConstraints())).append("\n\n");

        sb.append("[이전 추천 결과]\n");
        context.previousRecommendations().stream()
                .sorted(Comparator.comparing(exercise -> exercise.getId().getRank()))
                .forEach(exercise -> sb.append("- ").append(exercise.getId().getRank())
                        .append("순위: ").append(exercise.getExerciseName()).append("\n"));

        sb.append("\n현재 설문 응답이 이전 설문 대비 어떻게 달라졌는지 비교하여, ")
                .append("각 추천 운동의 improvements 필드에 '이전 대비 개선된 점'을 한국어 한 문장으로 작성하세요.\n\n");
    }

    private String describeExercisePurpose(Survey survey) {
        if (survey.getExercisePurpose() == null) {
            return "-";
        }
        String etc = survey.getExercisePurposeEtc();
        return survey.getExercisePurpose().name() + (etc != null && !etc.isBlank() ? "(" + etc + ")" : "");
    }

    private String describeExerciseSpot(Survey survey) {
        if (survey.getExerciseSpot() == null) {
            return "-";
        }
        String etc = survey.getExerciseSpotEtc();
        return survey.getExerciseSpot().name() + (etc != null && !etc.isBlank() ? "(" + etc + ")" : "");
    }

    private String describeConstraints(List<SurveyConstraint> constraints) {
        if (constraints == null || constraints.isEmpty()) {
            return "없음";
        }
        return constraints.stream()
                .map(constraint -> {
                    ConstraintType type = constraint.getId().getConstraintType();
                    String etc = constraint.getConstraintEtc();
                    if (type == ConstraintType.기타 && etc != null && !etc.isBlank()) {
                        return type.name() + "(" + etc + ")";
                    }
                    return type.name();
                })
                .collect(Collectors.joining(", "));
    }

    private String nullToDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
