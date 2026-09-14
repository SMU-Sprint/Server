package smu.sprint.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.domain.recommendation.client.GeminiClient;
import smu.sprint.domain.recommendation.client.RecommendationContext;
import smu.sprint.domain.recommendation.client.dto.GeminiRecommendationResult;
import smu.sprint.domain.recommendation.dto.RecommendationResponse;
import smu.sprint.domain.recommendation.dto.RecommendationResult;
import smu.sprint.domain.recommendation.entity.RecommendedExercise;
import smu.sprint.domain.recommendation.entity.RecommendedExerciseId;
import smu.sprint.domain.recommendation.repository.RecommendedExerciseRepository;
import smu.sprint.domain.survey.entity.Survey;
import smu.sprint.domain.survey.entity.SurveyConstraint;
import smu.sprint.domain.survey.repository.SurveyConstraintRepository;
import smu.sprint.domain.survey.repository.SurveyRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.code.RecommendationErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.exception.RecommendationException;
import smu.sprint.global.security.auth.CustomUserDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyConstraintRepository surveyConstraintRepository;
    private final RecommendedExerciseRepository recommendedExerciseRepository;
    private final GeminiClient geminiClient;

    // Gemini 호출이 끝날 때까지 DB 트랜잭션/커넥션을 붙잡아두지 않기 위해
    // 메서드 전체를 @Transactional로 감싸지 않음 / 각 리포지토리 호출은 Spring Data가 개별적으로 트랜잭션을 관리
    public RecommendationResult recommend(CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 추천은 항상 회원이 가장 최근에 작성한 설문을 대상으로 운동 추천을 실시하기
        Survey survey = surveyRepository.findTopByMemberIdOrderByCreatedAtDesc(member.getMember_id())
                .orElseThrow(() -> new RecommendationException(RecommendationErrorCode.SURVEY_NOT_FOUND));

        // 설문에 대해서 이미 운동 추천이 존재하는 경우
        List<RecommendedExercise> existing = recommendedExerciseRepository.findByIdSurveyIdOrderByIdRankAsc(survey.getSurveyId());
        if (!existing.isEmpty()) {
            return new RecommendationResult(RecommendationResponse.from(survey.getSurveyId(), existing), true);
        }

        if (member.getAge() == null || member.getGender() == null) {
            throw new RecommendationException(RecommendationErrorCode.PROFILE_INCOMPLETE);
        }

        // 제약 사항 조회
        List<SurveyConstraint> constraints = surveyConstraintRepository.findByIdSurveyId(survey.getSurveyId());

        // 이전 설문 조회
        Survey previousSurvey = surveyRepository
                .findTopByMemberIdAndSurveyIdLessThanOrderBySurveyIdDesc(member.getMember_id(), survey.getSurveyId())
                .orElse(null);

        // 직전 설문이 없으면(첫 설문) 빈 리스트로 처리 — RecommendationContext.hasPreviousContext()가
        // 이 값으로 "이전 이력 없음"을 판단해 AI에게 improvements를 null로 두도록 지시함
        List<SurveyConstraint> previousConstraints = previousSurvey == null ? List.of()
                : surveyConstraintRepository.findByIdSurveyId(previousSurvey.getSurveyId());
        List<RecommendedExercise> previousRecommendations = previousSurvey == null ? List.of()
                : recommendedExerciseRepository.findByIdSurveyIdOrderByIdRankAsc(previousSurvey.getSurveyId());

        // 현재와 이전 설문 데이터를 하나로 묶어서 AI 프롬프트 생성에 필요한 컨텍스트로 전달
        RecommendationContext context = new RecommendationContext(member, survey, constraints, previousSurvey, previousConstraints, previousRecommendations);

        GeminiRecommendationResult result = geminiClient.generateRecommendations(context);

        boolean hasPrevious = context.hasPreviousContext();
        List<RecommendedExercise> toSave = result.recommendations().stream()
                .map(item -> RecommendedExercise.builder()
                        .id(RecommendedExerciseId.builder()
                                .surveyId(survey.getSurveyId())
                                .rank(item.rank())
                                .build())
                        .exerciseName(item.exerciseName())
                        .reason(item.reason())
                        .improvements(hasPrevious ? item.improvements() : null)
                        .build())
                .toList();

        List<RecommendedExercise> saved = recommendedExerciseRepository.saveAll(toSave);

        return new RecommendationResult(RecommendationResponse.from(survey.getSurveyId(), saved), false);
    }
}
