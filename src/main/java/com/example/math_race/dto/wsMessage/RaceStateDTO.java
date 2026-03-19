package com.example.math_race.dto.wsMessage;

import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RacePlayer;
import com.example.math_race.race.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceStateDTO {
    private String name;
    private String roomCode;
    private int targetScore;
    private RaceStatus status;
    private List<PlayerProgressDTO> players;
    private HostDetailsDTO host;
    private long totalDurationMillis;

    public RaceStateDTO(RaceManager raceManager){
        this.name = raceManager.getSettings().getRaceName();
        this.roomCode = raceManager.getRoomCode();
        this.targetScore = raceManager.getSettings().getTargetScore();
        this.host = new HostDetailsDTO(raceManager.getHost());
        this.status = raceManager.getStatus();
        this.players = new ArrayList<>();
        this.totalDurationMillis = raceManager.getSettings().getTotalDurationMillis();
        for(RacePlayer racePlayer : raceManager.getPlayers().values()){
            this.players.add(new PlayerProgressDTO(racePlayer));
        }
    }
}
