package com.example.math_race.race;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceSettings {

    private String raceName;
    private int targetScore;

    public  RaceSettings() {
        setDefaultName();
    }

    public void setDefaultName() {
        raceName = "Race " + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
