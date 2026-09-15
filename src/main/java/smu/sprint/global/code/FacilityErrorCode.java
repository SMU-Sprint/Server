package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum FacilityErrorCode implements BaseErrorCode {

    INVALID_COORDINATE(HttpStatus.BAD_REQUEST, "FACILITY400_0", "latitude/longitude 누락 또는 형식 오류입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
