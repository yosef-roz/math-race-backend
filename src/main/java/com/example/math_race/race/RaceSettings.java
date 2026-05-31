package com.example.math_race.race;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceSettings {

    private String raceName;
    private int targetScore;
    private boolean isPrivate;

    private final long totalDurationTimeMs;

    public RaceSettings() {
       this("",400,true);
       setDefaultName();
    }

    public RaceSettings(String raceName, int targetScore, boolean isPrivate) {
        if (raceName == null || raceName.isEmpty()) {
            setDefaultName();
        }else {
            this.raceName = raceName;
        }
        this.targetScore = targetScore;
        this.totalDurationTimeMs = (long) targetScore * 600;
        this.isPrivate = isPrivate;
    }

    public void setDefaultName() {
        raceName = "Race " + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
