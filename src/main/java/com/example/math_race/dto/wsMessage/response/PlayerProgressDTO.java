package com.example.math_race.dto.wsMessage.response;

import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerProgressDTO {
    private String id;
    private String nickname;
    private int currentScore;
    private boolean isOnline;

    public PlayerProgressDTO(RacePlayer racePlayer, boolean showScore){
        this.id = racePlayer.getId();
        this.nickname = racePlayer.getNickname();
        this.currentScore = showScore ? racePlayer.getCurrentScore() : 0;
        this.isOnline = racePlayer.isConnected();
    }
}
