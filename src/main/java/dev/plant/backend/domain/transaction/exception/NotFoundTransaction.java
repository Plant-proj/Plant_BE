package dev.plant.backend.domain.transaction.exception;

import dev.plant.backend.global.ErrorCode;
import dev.plant.backend.global.exception.BusinessException;

public class NotFoundTransaction extends BusinessException {
    public NotFoundTransaction(ErrorCode errorCode) {super(errorCode);}
}

