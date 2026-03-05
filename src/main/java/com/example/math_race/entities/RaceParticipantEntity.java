package com.example.math_race.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RaceParticipantEntity extends BaseEntity {

    private String token;
    private UserEntity user;
    private String displayName;
    private int currentScore = 0;

    public RaceParticipantEntity(UserEntity user) {
        this.token = "123"; // הסופת מחולל טוקנים לכאן
        this.user = user;
    }

    public RaceParticipantEntity(String displayName) {
        this.token = "123"; // הסופת מחולל טוקנים לכאן
        this.displayName = displayName;
    }

    public boolean isGust() {
        return user == null;
    }
}