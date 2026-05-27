package com.example.math_race.dto.http.response;

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

    private String name;
    private String roomCode;
    private String hostNickname;
    private String hostUsername;
    private RaceStatus status;
    private long startTime;
    private int targetScore;
    private int participants;
    private String role;

    public RaceInfoResponse(RaceManager race, RaceAccount account) {
        this.name = race.getSettings().getRaceName();
        this.roomCode = race.getRoomCode();
        this.hostNickname = race.getHost().getNickname();
        this.hostUsername = race.getHost().getUser().getUsername();
        this.startTime = race.getCreatedAtMs();
        this.targetScore = race.getSettings().getTargetScore();
        this.participants = race.getPlayers().size();
        this.status = race.getStatus();
        this.role = account != null && race.isAccountIn(account.getId()) ?
                race.isHost(account.getId()) ? "host" : "player" : "none";
    }

    public RaceInfoResponse(RaceManager race) {
        this(race, null);
    }
}
