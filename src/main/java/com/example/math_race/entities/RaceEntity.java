package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RaceEntity extends BaseEntity {

    public static final int MAX_PARTICIPANTS = 1000;
    public static final int MAX_TARGET_SCORE = 1000;

    public enum RaceStatus {
        PENDING,
        ACTIVE,
        FINISHED,
    }

    private String roomCode;
    private String name;
    private UserEntity host;
    private int targetScore;
    private List<RaceParticipantEntity> participants;
    private RaceStatus status;

    public RaceEntity() {
        this.roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // הוספת מחולל קוד חדר
        this.status = RaceStatus.PENDING;
        this.name = "Race " + UUID.randomUUID().toString().substring(0, 4).toUpperCase(); // הוספת מחוללל שמות
        this.targetScore = MAX_TARGET_SCORE / 2;
        participants = new ArrayList<>();
    }

    public RaceEntity(String name, UserEntity user, int targetScore) {
        this();
        this.name = name;
        this.host = user;
        this.targetScore = targetScore;
    }

    public boolean isFinished() {
        return status == RaceStatus.FINISHED;
    }

    public boolean setPlayers(List<RaceParticipantEntity> newParticipants) {
        if (newParticipants.size() > MAX_PARTICIPANTS) {
            return false;
        }
        this.participants = newParticipants;
        return true;
    }

    public boolean addPlayer(RaceParticipantEntity participant) {
        if (participants.size() >= MAX_PARTICIPANTS) {
            return false;
        }
        participants.add(participant);
        return true;
    }

    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }
}
