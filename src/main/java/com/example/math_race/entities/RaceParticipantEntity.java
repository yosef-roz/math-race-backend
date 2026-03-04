package com.example.math_race.entities;

import lombok.Data;

@Data
public class RaceParticipantEntity extends BaseEntity {

    private String token;
    private UserEntity user;
    private String displayName;
    private int currentScore = 0;

    public boolean isGust() {
        return user == null;
    }
}