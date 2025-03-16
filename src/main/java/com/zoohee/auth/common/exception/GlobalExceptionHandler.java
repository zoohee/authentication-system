package com.zoohee.auth.common.exception;

import com.zoohee.auth.common.exception.exceptions.AdminAccessDeniedException;
import com.zoohee.auth.common.exception.exceptions.PasswordNotMatchedException;
import com.zoohee.auth.dto.response.ErrorResponse;
import com.zoohee.auth.common.exception.exceptions.DefaultException;
import com.zoohee.auth.common.exception.exceptions.UserNotFoundException;
import com.zoohee.auth.common.exception.exceptions.UsernameDuplicatedException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AdminAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAdminAccessDeniedException(AdminAccessDeniedException exception) {
        log.error("[GlobalExceptionHandler] [handleAdminAccessDeniedException] exception ::: {}", exception.getClass());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ErrorCode.ACCESS_DENIED, exception.getMessage()));
    }

    @ExceptionHandler(PasswordNotMatchedException.class)
    public ResponseEntity<ErrorResponse> handlePasswordNotMatchedException(PasswordNotMatchedException exception) {
        log.error("[GlobalExceptionHandler] [handlePasswordNotMatchedException] exception ::: {}", exception.getClass());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorCode.PASSWORD_NOT_MATCHED, exception.getMessage()));
    }

    @ExceptionHandler(UsernameDuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleUsernameDuplicatedException(UsernameDuplicatedException exception) {
        log.error("[GlobalExceptionHandler] [handleUsernameDuplicatedException] exception ::: {}", exception.getClass());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ErrorCode.USERNAME_DUPLICATED, exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        log.error("[GlobalExceptionHandler] [handleUserNotFoundException] exception ::: {}", exception.getClass());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.USER_NOT_EXISTS, exception.getMessage()));
    }

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(DefaultException exception) {
        log.error("[GlobalExceptionHandler] [handleGlobalException] exception ::: {}", exception.getClass());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.SERVER_ERROR, exception.getMessage()));
    }
}
