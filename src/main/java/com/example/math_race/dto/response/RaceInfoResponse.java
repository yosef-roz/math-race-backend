package com.example.math_race.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceInfoResponse {
    private boolean isHost;
    private String statusRace;
    private String nickname;
    private String name;
    private String roomCode;
    private int targetScore;
}
