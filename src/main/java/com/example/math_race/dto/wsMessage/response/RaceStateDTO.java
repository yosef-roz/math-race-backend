package com.example.math_race.dto.wsMessage.response;

import com.example.math_race.race.RaceAccount;
import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RacePlayer;
import com.example.math_race.race.RaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private long remainingTimeMs;
    private String yourAccountId;
    private long sentAt;

    public RaceStateDTO(RaceManager race, RaceAccount toAccount){
        this.name = race.getSettings().getRaceName();
        this.roomCode = race.getRoomCode();
        this.targetScore = race.getSettings().getTargetScore();
        this.host = new HostDetailsDTO(race.getHost());
        this.status = race.getStatus();
        this.players = new ArrayList<>();
        this.totalDurationMillis = race.getSettings().getTotalDurationTimeMs();
        this.remainingTimeMs = race.getCalculatedRemainingTime();
        this.yourAccountId = toAccount.getId();
        this.sentAt = System.currentTimeMillis();

        boolean toHostAccount = race.isHost(toAccount.getId());

        for(RacePlayer player : race.getPlayers().values()){
            this.players.add(new PlayerProgressDTO(race,player,toHostAccount || Objects.equals(player,toAccount),
                    Objects.equals(player,toAccount) || Objects.equals(race.getStatus(),RaceStatus.FINISHED)));
        }
    }
}
