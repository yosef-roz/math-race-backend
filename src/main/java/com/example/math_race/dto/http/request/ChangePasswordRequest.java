package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
public class ChangePasswordRequest {

    @NotBlank(message = "NewPassword is required")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])\\S+$",
            message = "Password must contain at least one digit, at least one letter, and no spaces"
    )
    private String newPassword;

    @NotBlank(message = "OldPassword is required")
    @Size(max = 255, message = "OldPassword length is invalid")
    private String oldPassword;
}
