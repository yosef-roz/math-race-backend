package com.example.math_race.dto.wsMessage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRaceNameRequest {

    @Size(max = 15, message = "race name cannot exceed 15 characters")
    @Pattern(regexp = "^(?:.*\\S){3}.*$", message = "race name must contain at least 3 actual characters")
    @Pattern(regexp = "^\\S.*\\S$", message = "race name must not start or end with a space")
    @Pattern(regexp = ".*[a-zA-Z].*[a-zA-Z].*", message = "race name must contain at least 2 English letters")
    private String raceName;
}
