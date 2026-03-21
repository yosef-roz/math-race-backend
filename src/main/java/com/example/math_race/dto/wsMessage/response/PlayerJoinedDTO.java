package com.example.math_race.dto.wsMessage.response;

import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerJoinedDTO {

    private PlayerProgressDTO player;

    public PlayerJoinedDTO(RacePlayer racePlayer){
        this.player = new PlayerProgressDTO(racePlayer);
    }
}
