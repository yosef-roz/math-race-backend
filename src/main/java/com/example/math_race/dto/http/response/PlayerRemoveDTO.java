package com.example.math_race.dto.http.response;

import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRemoveDTO {
    private String playerId;

    public  PlayerRemoveDTO(RacePlayer player){
        this.playerId = player.getId();
    }
}
