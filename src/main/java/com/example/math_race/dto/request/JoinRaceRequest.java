package com.example.math_race.dto.request;

import lombok.Data;

@Data
public class JoinRaceRequest {
    private String roomCode;
    private String nickname;
}
