package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthErrorCode implements BaseErrorCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH401_0", "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "AccessToken이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_2", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_3", "유효하지 않거나 만료된 RefreshToken입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
