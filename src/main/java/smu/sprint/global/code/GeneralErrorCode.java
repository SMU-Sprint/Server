package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralErrorCode implements BaseErrorCode {

    BAD_REQUEST_400(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN_403(HttpStatus.FORBIDDEN, "COMMON403", "접근이 금지되었습니다."),
    NOT_FOUND_404(HttpStatus.NOT_FOUND, "COMMON404", "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED_405(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않은 HTTP 메서드입니다."),
    CONFLICT_409(HttpStatus.CONFLICT, "COMMON409", "이미 존재하는 데이터이거나 요청이 현재 상태와 중복되어 충돌합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다."),

    // 유효성 검사 실패 코드
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400_0", "잘못된 파라미터 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
