package dev.plant.backend.global.exception;

import dev.plant.backend.global.ErrorCode;

public class NotFoundException extends BusinessException{
  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
