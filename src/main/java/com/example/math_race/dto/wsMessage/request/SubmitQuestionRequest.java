package com.example.math_race.dto.wsMessage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionRequest {

    @NotBlank(message = "Answer is required")
    @Size(max = 50, message = "Answer cannot exceed 50 characters")
    @Pattern(
            regexp = "^\\S(?:.*\\S)?$",
            message = "Answer must not start or end with a space"
    )
    private String answer;
}
