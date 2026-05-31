package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VerifyAccountRequest {

    @NotBlank(message = "Token is required")
    @Size(max = 255, message = "Token length is invalid")
    private String token;
}
