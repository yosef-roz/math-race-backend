package com.example.math_race.dto.wsMessage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JunctionChooseRequest {

    @NotBlank(message = "choice is required")
    @Size(min = 3, max = 15, message = "choice must be between 3 and 15 characters")
    private String choice;
}
