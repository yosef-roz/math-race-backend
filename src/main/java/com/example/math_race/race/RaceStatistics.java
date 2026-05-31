package com.example.math_race.race;

import lombok.Data;

import java.util.*;

@Data
public class RaceStatistics {

    // רף מינימלי למניעת זכייה מקרית
    private static final int MIN_SUCCESS_THRESHOLD = 3;

    private RacePlayer streakMaster;   // השחקן עם הרצף הכי ארוך
    private RacePlayer accuracyKing;   // השחקן הכי מדויק
    private RacePlayer speedDemon;     // השחקן הכי מהיר בתשובות נכונות בלבד

    private double accuracyPercentage;    // אחוז דיוק מירוץ ממוצע
    private double autostradaPercentage;       // אחוז בחירה באוטוסטרדה (מתוך ההחלטות שבוצעו)
    private double dirtRoadPercentage;         // אחוז בחירה בדרך עפר (מתוך ההחלטות שבוצעו)
    private long averageResponseTimeMs;   // זמן תגובה ממוצע כללי
    private int totalJunctionsOffered;         // סך כל הצמתים שהוצעו לשחקנים

    public RaceStatistics(RaceManager race) {
        List<RacePlayer> players = new ArrayList<>(race.getPlayers().values());
        if (players.isEmpty()) return;

        calculateAwards(players);
        calculateGlobalStats(players);
    }

    private void calculateAwards(List<RacePlayer> players) {
        this.streakMaster = players.stream()
                .max(Comparator.comparingInt(RacePlayer::getMaxRegularStreak))
                .orElse(null);

        this.accuracyKing = players.stream()
                .filter(p -> p.getRegularQAttempts() >= MIN_SUCCESS_THRESHOLD)
                .max(Comparator.comparingDouble(RaceStatistics::getPlayerAccuracyPercentage))
                .orElse(null);

        this.speedDemon = players.stream()
                .filter(p -> p.getRegularQSuccesses() >= MIN_SUCCESS_THRESHOLD)
                .min(Comparator.comparingDouble(RaceStatistics::getPlayerAverageSuccessSpeedMs))
                .orElse(null);
    }

    private void calculateGlobalStats(List<RacePlayer> players) {
        long totalAttempts = 0;
        long totalSuccesses = 0;
        long totalTime = 0;
        int totalAuto = 0;
        int totalDirt = 0;
        this.totalJunctionsOffered = 0;

        for (RacePlayer p : players) {
            totalAttempts += p.getRegularQAttempts();
            totalSuccesses += p.getRegularQSuccesses();
            totalTime += p.getRegularQTimeMs();
            totalAuto += p.getAutostradaChoices();
            totalDirt += p.getDirtRoadChoices();
            this.totalJunctionsOffered += p.getJunctionsOfferedCount();
        }

        if (totalAttempts > 0) {
            this.accuracyPercentage = ((double) totalSuccesses / totalAttempts) * 100;
            this.averageResponseTimeMs = totalTime / totalAttempts;
        }

        int totalDecisions = totalAuto + totalDirt;
        if (totalDecisions > 0) {
            this.autostradaPercentage = ((double) totalAuto / totalDecisions) * 100;
            this.dirtRoadPercentage = ((double) totalDirt / totalDecisions) * 100;
        }
    }

    public static double getPlayerAccuracyPercentage(RacePlayer p) {
        if (p == null || p.getRegularQAttempts() == 0) return 0;
        return ((double) p.getRegularQSuccesses() / p.getRegularQAttempts()) * 100;
    }

    public static double getPlayerAverageSuccessSpeedMs(RacePlayer p) {
        if (p == null || p.getRegularQSuccesses() == 0) return Double.MAX_VALUE;
        return (double) p.getRegularSuccessTimeMs() / p.getRegularQSuccesses();
    }

    public static int getPlayerMaxStreak(RacePlayer p) {
        return (p != null) ? p.getMaxRegularStreak() : 0;
    }
}
