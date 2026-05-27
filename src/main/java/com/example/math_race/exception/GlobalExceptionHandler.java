package com.example.math_race.exception;

import com.example.math_race.dto.http.ApiResponse;
import com.example.math_race.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private final WebSocketService webSocketService;

    @Autowired
    public GlobalExceptionHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(LogicException ex) {
        ApiResponse<Void> body = ApiResponse.error(ex.getErrorCode());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String specificMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse<Void> body = ApiResponse.error(ErrorCode.INVALID_INPUT, specificMessage);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    @MessageExceptionHandler(org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException.class)
    public void handleWebSocketValidationExceptions(
            org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException ex,
            StompHeaderAccessor accessor) {

        String cleanMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        webSocketService.sendErrorToQueueSession(
                WebSocketService.QUEUE_RACE_FEEDBACK,
                ErrorCode.INVALID_INPUT,
                cleanMessage,
                accessor
        );
    }

    @MessageExceptionHandler(Exception.class)
    public void handleAllOtherWebSocketExceptions(Exception ex, StompHeaderAccessor accessor) {
        ex.printStackTrace();
        webSocketService.sendErrorToQueueSession(
                WebSocketService.QUEUE_NOTIFICATIONS,
                ErrorCode.INTERNAL_ERROR,
                accessor
        );
    }

    @MessageExceptionHandler(LogicException.class)
    public void handleLogicException(LogicException ex, StompHeaderAccessor accessor) {

        webSocketService.sendErrorToQueueSession(
                WebSocketService.QUEUE_NOTIFICATIONS,
                ex.getErrorCode(),
                accessor
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse<Void> body = ApiResponse.error(ErrorCode.INVALID_INPUT, "Invalid JSON format in the request body.");

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
