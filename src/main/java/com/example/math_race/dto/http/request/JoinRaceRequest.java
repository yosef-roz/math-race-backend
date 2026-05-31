package com.example.math_race.dto.http.request;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class JoinRaceRequest {

    @Size(max = 15, message = "nickname cannot exceed 15 characters")
    @Pattern(regexp = "^(?:.*\\S){3}.*$", message = "nickname must contain at least 3 actual characters")
    @Pattern(regexp = "^\\S.*\\S$", message = "nickname must not start or end with a space")
    @Pattern(regexp = ".*[a-zA-Z].*[a-zA-Z].*", message = "nickname must contain at least 2 English letters")
    private String nickname;
}
