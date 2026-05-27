package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerifyAccountRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
