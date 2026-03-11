package com.example.math_race.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    String token;
    String newPassword;
}
