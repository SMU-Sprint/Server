package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum RecommendationErrorCode implements BaseErrorCode {

    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "RECOMMEND404_0", "존재하지 않는 설문입니다."),
    PROFILE_INCOMPLETE(HttpStatus.PRECONDITION_FAILED, "RECOMMEND412_0", "성별 또는 연령 정보가 없어 추천을 생성할 수 없습니다. 회원 정보를 먼저 입력해주세요."),
    AI_CALL_FAILED(HttpStatus.BAD_GATEWAY, "RECOMMEND502_0", "AI 추천 생성에 실패했습니다. 잠시 후 다시 시도해주세요."),
    AI_RESPONSE_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RECOMMEND500_0", "추천 결과 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
