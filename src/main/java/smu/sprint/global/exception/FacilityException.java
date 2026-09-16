package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class FacilityException extends CustomException {

    public FacilityException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
