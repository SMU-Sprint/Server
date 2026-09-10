package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum EmailVerificationErrorCode implements BaseErrorCode {

    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "MAIL404_0", "발급된 인증 코드가 없습니다. 인증 코드를 다시 요청해주세요."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "MAIL400_0", "인증 코드가 만료되었습니다. 인증 코드를 다시 요청해주세요."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "MAIL400_1", "인증 코드가 일치하지 않습니다."),
    VERIFICATION_REQUEST_TOO_FREQUENT(HttpStatus.TOO_MANY_REQUESTS, "MAIL429_0", "인증 코드는 잠시 후 다시 요청할 수 있습니다."),
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL500_0", "인증 메일 발송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
