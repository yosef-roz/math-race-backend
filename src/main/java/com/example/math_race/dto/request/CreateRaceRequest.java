package com.example.math_race.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

//@Data
//public class CreateRaceRequest {
//    private String name;
//
//    @NotNull(message = "Target score is required")
//    @Positive(message = "Target score must be a positive number")
//    private Integer targetScore;
//
//    private String nickname;
//}

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateRaceRequest {

    // אם לא נשלח - null, וזה תקין. אם נשלח - חלים עליו החוקים.
    @Size(max = 20, message = "name cannot exceed 20 characters")
    @Pattern(regexp = "^(?:.*\\S){3}.*$", message = "name must contain at least 3 actual characters")
    private String name;

    @NotNull(message = "Target score is required")
    @Min(value = 400, message = "Target score must be at least 400")
    @Max(value = 1500, message = "Target score cannot exceed 1500")
    private Integer targetScore;

    // כנ"ל - אופציונלי, אבל אם סופק חייב להיות תקין
    @Size(max = 20, message = "nickname cannot exceed 20 characters")
    @Pattern(regexp = "^(?:.*\\S){3}.*$", message = "nickname must contain at least 3 actual characters")
    private String nickname;
}
