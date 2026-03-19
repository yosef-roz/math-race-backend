package com.example.math_race.dto.response;

import com.example.math_race.race.RaceAccount;
import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceInfoResponse {
    private boolean isHost;
    private String nickname;
    private RaceStatus status;
    private String name;
    private String roomCode;
    private int targetScore;

    public  RaceInfoResponse(RaceAccount account, RaceManager raceManager) {
        this.isHost = raceManager.isHost(account.getId());
        this.nickname = account.getNickname();
        this.status = raceManager.getStatus();
        this.name = raceManager.getSettings().getRaceName();
        this.roomCode = raceManager.getRoomCode();
        this.targetScore = raceManager.getSettings().getTargetScore();
    }
}
