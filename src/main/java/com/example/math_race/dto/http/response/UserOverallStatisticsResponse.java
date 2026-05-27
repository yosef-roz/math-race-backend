package com.example.math_race.dto.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOverallStatisticsResponse {

    private int NumberOfRace;
    private int NumberOfVictories;
    private int maxStreak;
    private double AvgAccuracy;
    private double AvgSuccessTimeMs;

}
