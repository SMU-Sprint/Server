package smu.sprint.global.exception;

import lombok.Getter;
import smu.sprint.global.code.BaseErrorCode;

@Getter
public class CustomException extends RuntimeException {

    public CustomException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode.getMessage());
        this.baseErrorCode = baseErrorCode;
    }

    private final BaseErrorCode baseErrorCode;

}
