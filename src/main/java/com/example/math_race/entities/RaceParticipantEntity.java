package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceParticipantEntity extends BaseEntity {

    private String token;
    private UserEntity user;
    private String nickname;
    private int currentScore = 0;

    public RaceParticipantEntity() {
        this.token = UUID.randomUUID().toString().substring(0, 4); // הוספת מחולל טוקנים
    }

    public RaceParticipantEntity(UserEntity user, String nickname) {
        this();
        this.user = user;
        this.nickname = nickname;
    }

    public RaceParticipantEntity(UserEntity user) {
        this(user, user.getUsername());
    }

    public RaceParticipantEntity(String nickname) {
        this(null, nickname);
    }

    public boolean isGust() {
        return user == null;
    }
}