package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "NewPassword is required")
    private String newPassword;
}
