package com.example.math_race.race;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class JunctionEngine {

    private static final int MAX_ROLL = 1000;

    public boolean shouldTriggerJunction(RacePlayer player, RaceManager manager) {
        int targetScore = manager.getSettings().getTargetScore();

        double progress = (double) player.getCurrentScore() / targetScore;
        if (progress < 0.15) return false;

        int questionsSinceLastJunction = player.getRegularQAttempts() - player.getLastJunctionRegularQCount();
          if (questionsSinceLastJunction < 5) return false;

        int threshold = 100;

        List<RacePlayer> players = manager.getRankedPlayers();
        int leaderScore = players.get(players.size()-1).getCurrentScore();
        int gapFromLeader = leaderScore - player.getCurrentScore();

        if (gapFromLeader > (targetScore * 0.15)) {
            threshold += 200;
        }

        if (player.getCurrentScore() == leaderScore) {
            threshold -= 50;
        }

        int streakBonus = Math.min(player.getCurrentRegularStreak() * 20, 100);
        threshold += streakBonus;

        double timeElapsedPercent = 1.0 - ((double) manager.getCalculatedRemainingTime() / manager.getSettings().getTotalDurationTimeMs());
        threshold += (int) (timeElapsedPercent * 150);

        threshold = Math.max(50, Math.min(threshold, 600));

        int roll = ThreadLocalRandom.current().nextInt(MAX_ROLL);
        return roll < threshold;
    }
}
