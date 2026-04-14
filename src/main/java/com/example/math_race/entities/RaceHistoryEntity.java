package com.example.math_race.entities;

import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RacePlayer;
import com.example.math_race.race.RaceStatistics;
import com.example.math_race.race.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceHistoryEntity extends BaseEntity {

    private String name;
    private String roomCode;
    private String hostId;
    private String hostNickname;
    private int targetScore;
    private RaceStatus status;
    private long createdAtMs;
    private long endedAtMs;
    private long totalDurationMillis;
    private long totalPausedDurationMillis;
    private int totalParticipants;

    private String streakMasterId;
    private String streakMasterNickname;
    private int streakMasterAmount;

    private String accuracyKingId;
    private String accuracyKingNickname;
    private double accuracyKingPercentage;

    private String speedDemonId;
    private String speedDemonNickname;
    private double speedDemonTimeMs;

    private double accuracyPercentage;
    private double autostradaPercentage;
    private double dirtRoadPercentage;
    private long averageResponseTimeMs;
    private int totalJunctionsOffered;

    public RaceHistoryEntity(RaceManager race) {
        super();
        this.name = race.getSettings().getRaceName();
        this.roomCode = race.getRoomCode();
        this.targetScore = race.getSettings().getTargetScore();
        this.status = race.getStatus();
        this.createdAtMs = race.getCreatedAtMs();
        this.endedAtMs = race.getEndedAtMs();
        this.totalDurationMillis = race.getSettings().getTotalDurationTimeMs();
        this.totalPausedDurationMillis = race.getTotalPausedDurationTimeMs();
        this.totalParticipants = race.getPlayers().size();

        this.hostId = race.getHost().getId();
        this.hostNickname = race.getHost().getNickname();

        race.updateStatistics();
        RaceStatistics statistics = race.getStatistics();

        this.accuracyPercentage = statistics.getAccuracyPercentage();
        this.autostradaPercentage = statistics.getAutostradaPercentage();
        this.dirtRoadPercentage = statistics.getDirtRoadPercentage();
        this.averageResponseTimeMs = statistics.getAverageResponseTimeMs();
        this.totalJunctionsOffered = statistics.getTotalJunctionsOffered();

        RacePlayer streakMaster = statistics.getStreakMaster();
        if (streakMaster != null) {
            this.streakMasterId = streakMaster.getId();
            this.streakMasterNickname = streakMaster.getNickname();
            this.streakMasterAmount = RaceStatistics.getPlayerMaxStreak(streakMaster);
        }

        RacePlayer accuracyKing = statistics.getAccuracyKing();
        if (accuracyKing != null) {
            this.accuracyKingId = accuracyKing.getId();
            this.accuracyKingNickname = accuracyKing.getNickname();
            this.accuracyKingPercentage = RaceStatistics.getPlayerAccuracyPercentage(accuracyKing);
        }

        RacePlayer speedDemon = statistics.getSpeedDemon();
        if (speedDemon != null) {
            this.speedDemonId = speedDemon.getId();
            this.speedDemonNickname = speedDemon.getNickname();
            this.speedDemonTimeMs = RaceStatistics.getPlayerAverageSuccessSpeedMs(speedDemon);
        }
    }
}