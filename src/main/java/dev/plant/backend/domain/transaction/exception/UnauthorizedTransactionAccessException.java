package dev.plant.backend.domain.transaction.exception;

import dev.plant.backend.global.ErrorCode;
import dev.plant.backend.global.exception.BusinessException;

public class UnauthorizedTransactionAccessException extends BusinessException {
    public UnauthorizedTransactionAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
