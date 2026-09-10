package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class MemberException extends CustomException {

    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
