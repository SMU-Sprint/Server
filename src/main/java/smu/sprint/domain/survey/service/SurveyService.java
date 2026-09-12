package smu.sprint.domain.survey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.domain.survey.dto.SurveyCreateRequest;
import smu.sprint.domain.survey.dto.SurveyCreateResponse;
import smu.sprint.domain.survey.entity.ConstraintType;
import smu.sprint.domain.survey.entity.ExercisePurpose;
import smu.sprint.domain.survey.entity.ExerciseSpot;
import smu.sprint.domain.survey.entity.Survey;
import smu.sprint.domain.survey.entity.SurveyConstraint;
import smu.sprint.domain.survey.entity.SurveyConstraintId;
import smu.sprint.domain.survey.repository.SurveyConstraintRepository;
import smu.sprint.domain.survey.repository.SurveyRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.code.SurveyErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.exception.SurveyException;
import smu.sprint.global.security.auth.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyConstraintRepository surveyConstraintRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public SurveyCreateResponse createSurvey(CustomUserDetails customUserDetails, SurveyCreateRequest request) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        validateEtcFields(request);

        Survey survey = Survey.builder()
                .memberId(member.getMember_id())
                .exercisePurpose(request.exercisePurpose())
                .exercisePurposeEtc(request.exercisePurposeEtc())
                .exerciseExperienceFlag(request.exerciseExperienceFlag())
                .exerciseExperienceDetail(request.exerciseExperienceDetail())
                .preferredSport(request.preferredSport())
                .occupationType(request.occupationType())
                .vigorousDays(request.vigorousDays())
                .vigorousDurationMinutes(request.vigorousDurationMinutes())
                .moderateDays(request.moderateDays())
                .moderateDurationMinutes(request.moderateDurationMinutes())
                .walkingDays(request.walkingDays())
                .walkingDurationMinutes(request.walkingDurationMinutes())
                .workStartTime(request.workStartTime())
                .workEndTime(request.workEndTime())
                .exerciseSpot(request.exerciseSpot())
                .exerciseSpotEtc(request.exerciseSpotEtc())
                .fatigueFlag(request.fatigueFlag())
                .stairClimbFlag(request.stairClimbFlag())
                .walk300mFlag(request.walk300mFlag())
                .weightLossFlag(request.weightLossFlag())
                .build();

        Survey saved = surveyRepository.save(survey);

        List<SurveyConstraint> constraints = new ArrayList<>();
        for (ConstraintType constraintType : request.constraintTypes()) {
            SurveyConstraint constraint = SurveyConstraint.builder()
                    .id(SurveyConstraintId.builder()
                            .surveyId(saved.getSurveyId())
                            .constraintType(constraintType)
                            .build())
                    .constraintEtc(constraintType == ConstraintType.기타 ? request.constraintEtc() : null)
                    .build();
            constraints.add(constraint);
        }
        surveyConstraintRepository.saveAll(constraints);

        return SurveyCreateResponse.from(saved, constraints);
    }

    @Transactional(readOnly = true)
    public SurveyCreateResponse getLatestSurvey(CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Survey survey = surveyRepository.findTopByMemberIdOrderByCreatedAtDesc(member.getMember_id())
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

        List<SurveyConstraint> constraints = surveyConstraintRepository.findByIdSurveyId(survey.getSurveyId());

        return SurveyCreateResponse.from(survey, constraints);
    }

    private void validateEtcFields(SurveyCreateRequest request) {
        // 운동의 목적이 기타인 경우 기타 항목을 필수로 입력하도록 하기
        if (request.exercisePurpose() == ExercisePurpose.기타
                && (request.exercisePurposeEtc() == null || request.exercisePurposeEtc().isBlank())) {
            throw new SurveyException(SurveyErrorCode.INVALID_ETC_REQUIRED);
        }

        // 운동 장소가 기타인 경우 기타 항목을 필수로 입력하도록 하기
        if (request.exerciseSpot() == ExerciseSpot.기타
                && (request.exerciseSpotEtc() == null || request.exerciseSpotEtc().isBlank())) {
            throw new SurveyException(SurveyErrorCode.INVALID_ETC_REQUIRED);
        }

        // 운동 경험이 true라면 운동 경험 항목을 필수로 입력하도록 하기
        if (request.exerciseExperienceFlag() && (request.exerciseExperienceDetail() == null || request.exerciseExperienceDetail().isBlank())) {
            throw new SurveyException(SurveyErrorCode.INVALID_EXERCISE_TRUE_REQUIRED);
        }

        // 제약사항에 기타가 포함된 경우 기타 항목을 필수로 입력하도록 하기
        if (request.constraintTypes().contains(ConstraintType.기타)
                && (request.constraintEtc() == null || request.constraintEtc().isBlank())) {
            throw new SurveyException(SurveyErrorCode.INVALID_CONSTRAINT_ETC_REQUIRED);
        }
    }
}