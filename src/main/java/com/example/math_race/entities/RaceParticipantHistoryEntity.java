package com.example.math_race.entities;

import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceParticipantHistoryEntity extends BaseEntity {

    private RaceHistoryEntity race;
    private UserEntity user;
    private String guestId;
    private String nickname;
    private int finalScore;
    private int rank;

    private int regularQAttempts;
    private int regularQSuccesses;
    private long regularQTimeMs;
    private int maxRegularStreak;
    private long regularSuccessTimeMs;

    private int junctionsOfferedCount;
    private int autostradaChoices;
    private int dirtRoadChoices;

    public RaceParticipantHistoryEntity(RaceHistoryEntity raceHistoryEntity, RacePlayer player, int rank) {
        super();
        this.race = raceHistoryEntity;
        this.user = player.getUser();
        this.guestId = this.user == null ? player.getId() : null;
        this.nickname = player.getNickname();
        this.finalScore = player.getCurrentScore();
        this.rank = rank;

        this.regularQAttempts = player.getRegularQAttempts();
        this.regularQSuccesses = player.getRegularQSuccesses();
        this.regularQTimeMs = player.getRegularQTimeMs();
        this.maxRegularStreak = player.getMaxRegularStreak();
        this.regularSuccessTimeMs = player.getRegularSuccessTimeMs();

        this.junctionsOfferedCount = player.getJunctionsOfferedCount();
        this.autostradaChoices = player.getAutostradaChoices();
        this.dirtRoadChoices = player.getDirtRoadChoices();
    }

    public boolean isGuest() {
        return user == null;
    }

    public double getCalculatedAccuracy() {
        return regularQAttempts > 0 ? ((double) regularQSuccesses / regularQAttempts) * 100 : 0;
    }

    public double getCalculatedAverageSuccessTimeMs() {
        return regularQSuccesses > 0 ? (double) regularSuccessTimeMs / regularQSuccesses : 0;
    }
}