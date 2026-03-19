package com.example.math_race.dto.wsMessage;

import com.example.math_race.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage<T> {
    private String type;
    private String content;
    private Integer code;
    private String message;
    private T data;

    public static WsMessage<Void> createError(ErrorCode error) {
        return WsMessage.<Void>builder()
                .type("ERROR")
                .content(error.name())
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    public static <T> WsMessage<T> generalMessage(String message) {
        return WsMessage.<T>builder()
                .type("GENERAL_MESSAGE")
                .message(message)
                .build();
    }

    public static <T> WsMessage<T> success(String type, T data) {
        return WsMessage.<T>builder()
                .type(type)
                .data(data)
                .build();
    }
}