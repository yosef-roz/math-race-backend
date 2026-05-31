package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])\\S+$",
            message = "Username must contain at least one letter and no spaces"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])\\S+$",
            message = "Password must contain at least one digit, at least one letter, and no spaces"
    )
    private String password;
}
