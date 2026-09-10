package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class EmailVerificationException extends CustomException {

    public EmailVerificationException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
