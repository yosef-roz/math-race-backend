package com.example.math_race.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private int dayToSaveToken;
    private String username;
    private String email;
}
