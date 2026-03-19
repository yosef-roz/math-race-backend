package com.example.math_race.dto.wsMessage;

import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerConnectionDTO {
    private String id;
    private boolean isOnline;

    public PlayerConnectionDTO(RacePlayer racePlayer) {
        this.id = racePlayer.getId();
        this.isOnline = racePlayer.isConnected();
    }
}
