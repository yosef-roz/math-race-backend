package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RaceEntity extends BaseEntity {

    public static final int MAX_PARTICIPANTS = 50;
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
        status = RaceStatus.PENDING;
        participants = new ArrayList<>();
    }

    public boolean isFinished() {
        return status == RaceStatus.FINISHED;
    }

    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }
}
