package com.example.math_race.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinRaceResponse {
    private String nickname;
    private String name;
    private String code;
    private String type;
    private int targetScore;
}
