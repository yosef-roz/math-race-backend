package com.example.math_race.race;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceSettings {

    private String raceName;
    private int targetScore;

    private final long totalDurationMillis;

    public RaceSettings() {
       this("",RaceValidator.MIN_SCORES);
       setDefaultName();
    }

    public RaceSettings(String raceName, int targetScore) {
        this.raceName = raceName;
        this.targetScore = targetScore;
        this.totalDurationMillis = (long) targetScore * 600;
    }

    public void setDefaultName() {
        raceName = "Race " + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
