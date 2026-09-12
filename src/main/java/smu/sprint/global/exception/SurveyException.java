package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class SurveyException extends CustomException {

    public SurveyException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}