package com.example.math_race.dto.http.response;

import com.example.math_race.entities.RaceHistoryEntity;
import com.example.math_race.entities.RaceParticipantHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceHistoryDetailsResponse {

    private RaceHistorySummaryResponse summary;

    private double accuracyPercentage;
    private double autostradaPercentage;
    private double dirtRoadPercentage;
    private long averageResponseTimeMs;
    private int totalJunctionsOffered;

    private List<RaceParticipantHistoryResponse> playerHistory;


    public RaceHistoryDetailsResponse(RaceHistoryEntity raceHistoryEntity, List<RaceParticipantHistoryEntity> players, boolean isHost, Integer rank) {
        this.summary = new RaceHistorySummaryResponse(raceHistoryEntity,isHost,rank);

        this.accuracyPercentage = raceHistoryEntity.getAccuracyPercentage();
        this.autostradaPercentage = raceHistoryEntity.getAutostradaPercentage();
        this.dirtRoadPercentage = raceHistoryEntity.getDirtRoadPercentage();
        this.averageResponseTimeMs = raceHistoryEntity.getAverageResponseTimeMs();
        this.totalJunctionsOffered = raceHistoryEntity.getTotalJunctionsOffered();

        this.playerHistory = new ArrayList<>();
        for (RaceParticipantHistoryEntity player : players) {
            playerHistory.add(new RaceParticipantHistoryResponse(raceHistoryEntity,player));
        }
    }
}
