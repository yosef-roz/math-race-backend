package com.example.math_race.exception;

import com.example.math_race.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(LogicException ex) {
        ApiResponse<Void> body = ApiResponse.error(ex.getErrorCode());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllOtherExceptions(Exception ex) {
        ex.printStackTrace();

        ApiResponse<Void> body = ApiResponse.error(ErrorCode.INTERNAL_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
        ApiResponse<Void> body = ApiResponse.error(ErrorCode.NOT_FOUND);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }
}