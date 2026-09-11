package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SurveyErrorCode implements BaseErrorCode {

    INVALID_ETC_REQUIRED(HttpStatus.BAD_REQUEST, "SURVEY400_0", "'기타'를 선택한 경우 상세 내용을 입력해야 합니다."),
    INVALID_EXERCISE_TRUE_REQUIRED(HttpStatus.BAD_REQUEST, "SURVEY400_1", "운동 경험이 있는 경우 운동을 입력해야 합니다."),
    INVALID_CONSTRAINT_ETC_REQUIRED(HttpStatus.BAD_REQUEST, "SURVEY400_2", "'기타'를 선택한 경우 제약사항 상세 내용을 입력해야 합니다."),
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY404_0", "작성된 설문이 없습니다. 설문을 먼저 작성해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}