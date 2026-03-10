package com.example.math_race.dto.response;

import com.example.math_race.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private Integer errorCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String msg) {
        return new ApiResponse<>(true, null, msg , data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully!");
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(),null);
    }
}