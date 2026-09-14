package smu.sprint.global.exception;

import smu.sprint.global.code.BaseErrorCode;

public class RecommendationException extends CustomException {

    public RecommendationException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
