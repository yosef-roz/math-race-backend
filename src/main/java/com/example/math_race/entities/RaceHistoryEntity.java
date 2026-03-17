package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceHistoryEntity extends BaseEntity {

    private String roomCode;
    private String name;
    private UserEntity host;
    private int targetScore;

    public RaceHistoryEntity() {
        this.name = "Race " + UUID.randomUUID().toString().substring(0, 4).toUpperCase(); // הוספת מחוללל שמות
        this.targetScore = 1000;
    }

    public RaceHistoryEntity(String name, UserEntity user, int targetScore) {
        this();
        this.name = name;
        this.host = user;
        this.targetScore = targetScore;
    }
}
