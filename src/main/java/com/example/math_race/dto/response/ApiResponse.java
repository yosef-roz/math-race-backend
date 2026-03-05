package com.example.math_race.dto.response;

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
    private long timestamp;
    private T data;

    public static <T> ApiResponse<T> ok(T data, String msg) {
        return new ApiResponse<>(true, null, msg , System.currentTimeMillis(), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, "Operation completed successfully!");
    }

    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(false, code, msg, System.currentTimeMillis(), null);
    }
}