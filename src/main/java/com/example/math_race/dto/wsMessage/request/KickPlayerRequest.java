package com.example.math_race.dto.wsMessage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickPlayerRequest {

    @NotBlank(message = "player id is required")
    @Size(max = 255, message = "player id length is invalid")
    private String playerId;
}
