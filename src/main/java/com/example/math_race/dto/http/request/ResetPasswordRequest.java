package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    String token;

    @NotBlank(message = "NewPassword is required")
    String newPassword;
}
