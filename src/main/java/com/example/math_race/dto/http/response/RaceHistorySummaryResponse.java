package com.example.math_race.dto.http.response;

import com.example.math_race.entities.RaceHistoryEntity;
import com.example.math_race.race.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceHistorySummaryResponse {

    private String id;
    private String name;
    private long createdAtMs;
    private int totalParticipants;
    private int targetScore;
    private RaceStatus status;
    private String hostNickname;
    private String streakMasterNickname;
    private int streakMasterAmount;
    private String accuracyKingNickname;
    private double accuracyKingPercentage;
    private String speedDemonNickname;
    private double speedDemonTimeMs;

    private boolean isHost;
    private Integer rank;

    public RaceHistorySummaryResponse(RaceHistoryEntity raceHistory, boolean isHost, Integer rank){
        this.id = raceHistory.getId().toString();
        this.name = raceHistory.getName();
        this.createdAtMs = raceHistory.getCreatedAtMs();
        this.totalParticipants = raceHistory.getTotalParticipants();
        this.targetScore = raceHistory.getTargetScore();
        this.status = raceHistory.getStatus();
        this.hostNickname = raceHistory.getHostNickname();
        this.streakMasterNickname = raceHistory.getStreakMasterNickname();
        this.streakMasterAmount = raceHistory.getStreakMasterAmount();
        this.accuracyKingNickname =  raceHistory.getAccuracyKingNickname();
        this.accuracyKingPercentage = raceHistory.getAccuracyKingPercentage();
        this.speedDemonNickname = raceHistory.getSpeedDemonNickname();
        this.speedDemonTimeMs = raceHistory.getSpeedDemonTimeMs();

        this.isHost = isHost;
        this.rank = rank;
    }
}
