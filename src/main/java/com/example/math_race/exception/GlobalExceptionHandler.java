package com.example.math_race.exception;

import com.example.math_race.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(LogicException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse<Void> body = ApiResponse.error(
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }
}
