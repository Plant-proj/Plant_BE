package dev.plant.backend.global.exception;

import dev.plant.backend.global.ErrorCode;

public class InvalidArgumentException extends BusinessException {
  public InvalidArgumentException(ErrorCode errorCode) {
    super(errorCode);
  }
}
