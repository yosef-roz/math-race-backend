package com.example.math_race.dto.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsernameRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])\\S+$",
            message = "Username must contain at least one letter and no spaces"
    )
    private String username;
}
