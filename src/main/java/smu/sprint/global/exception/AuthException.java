package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class AuthException extends CustomException {

    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
