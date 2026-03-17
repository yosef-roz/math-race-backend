package com.example.math_race.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRaceResponse {
    private String name;
    private String code;
    private int targetScore;
    private String nickname;
}
