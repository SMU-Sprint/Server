package smu.sprint.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberErrorCode implements BaseErrorCode{

    SAME_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER400_0", "기존과 동일한 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER400_1", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    PASSWORD_NOT_CHANGED(HttpStatus.BAD_REQUEST, "MEMBER400_2",
            "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "MEMBER401_0", "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_0", "사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "저장된 RefreshToken을 찾을 수 없습니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "MEMBER409_0", "중복된 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
