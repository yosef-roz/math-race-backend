package com.example.math_race.dto.request;

import lombok.Data;

@Data
public class CreateRaceRequest {
    private String name;
    private Integer targetScore;
    private String nickname;
}
