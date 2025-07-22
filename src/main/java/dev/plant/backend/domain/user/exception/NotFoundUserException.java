package dev.plant.backend.domain.user.exception;

import dev.plant.backend.global.ErrorCode;
import dev.plant.backend.global.exception.BusinessException;

public class NotFoundUserException extends BusinessException {
    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
