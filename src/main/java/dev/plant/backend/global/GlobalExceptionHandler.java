package dev.plant.backend.global;

import dev.plant.backend.domain.transaction.exception.NotFoundTransaction;
import dev.plant.backend.domain.transaction.exception.UnauthorizedTransactionAccessException;
import dev.plant.backend.domain.user.exception.NotFoundUserException;
import dev.plant.backend.global.exception.ApiException;
import dev.plant.backend.global.exception.InvalidArgumentException;
import dev.plant.backend.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("handelException", exception);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //400 Bad Request
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        ErrorResponse errorResponse = ErrorResponse.of(exception.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(InvalidArgumentException exception) {
        log.error("handleInvalidArgumentException", exception);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    //404 Not Found
    @ExceptionHandler({NotFoundUserException.class, NotFoundTransaction.class})
    public ResponseEntity<ErrorResponse> handleNotFoundUser(NotFoundException exception) {
        log.error("handleNotFound", exception);
        ErrorResponse errorResponse = ErrorResponse.of(exception.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    //401 Unauthorized
    @ExceptionHandler(UnauthorizedTransactionAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedTransactionAccessException(UnauthorizedTransactionAccessException exception) {
        log.error("handleUnauthorizedTransactionAccessException", exception);
        ErrorResponse errorResponse = ErrorResponse.of(exception.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
