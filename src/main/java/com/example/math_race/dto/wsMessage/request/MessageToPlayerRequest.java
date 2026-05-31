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
public class MessageToPlayerRequest {

    @NotBlank(message = "player id is required")
    @Size(max = 255, message = "player id length is invalid")
    private String playerId;

    @NotBlank(message = "message is required")
    @Size(max = 500, message = "message cannot exceed 500 characters")
    @Pattern(
            regexp = "^\\S(?:.*\\S)?$",
            message = "Message must not start or end with a space"
    )
    private String message;
}
