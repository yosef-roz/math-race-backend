package com.example.math_race.dto.http.response;

import com.example.math_race.entities.RaceHistoryEntity;
import com.example.math_race.entities.RaceParticipantHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceParticipantHistoryResponse {
    private boolean isGuest;
    private String userName;

    private String nickname;
    private int finalScore;
    private int rank;

    private boolean isStreakMaster;
    private boolean isAccuracyKing;
    private boolean isSpeedDemon;

    public RaceParticipantHistoryResponse(RaceHistoryEntity raceHistoryEntity, RaceParticipantHistoryEntity player) {
        this.isGuest = player.isGuest();
        if (!player.isGuest() && !player.getUser().isDeleted()) {
            userName = player.getUser().getUsername();
        }
        this.nickname = player.getNickname();
        this.finalScore = player.getFinalScore();
        this.rank = player.getRank();

        String playerId = isGuest ? player.getGuestId() : player.getUser().getId().toString();

        this.isStreakMaster = Objects.equals(raceHistoryEntity.getStreakMasterId(), playerId);
        this.isAccuracyKing = Objects.equals(raceHistoryEntity.getAccuracyKingId(), playerId);
        this.isSpeedDemon = Objects.equals(raceHistoryEntity.getSpeedDemonId(), playerId);
    }
}
