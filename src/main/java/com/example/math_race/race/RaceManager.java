package com.example.math_race.race;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public class RaceManager {

    public static final int MAX_PLAYERS = 20;

    private final RaceSettings settings;
    private String roomCode;
    private RaceHost host;
    private final ArrayList<RacePlayer> players;
    private RaceStatus status;

    public RaceManager(RaceSettings raceSettings) {
        this.settings = raceSettings;
        updateRoomCode();
        this.players = new ArrayList<>();
        this.status = RaceStatus.PENDING;
    }

    public void joinRace(RacePlayer player) {
        if (players.contains(player)) {
            RacePlayer oldPlayer = players.get(players.indexOf(player));
            oldPlayer.setSessionActive(player.getSessionActive());
            oldPlayer.setNickname(player.getNickname());
            return;
        }

        if (players.size() >= MAX_PLAYERS) {
            throw new LogicException(ErrorCode.RACE_MAX_PLAYERS_EXCEEDED);
        }

        players.add(player);
    }

    public RacePlayer getPlayer(String playerId) {
        for (RacePlayer player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    public RaceAccount getAccount(String accountId) {
        if (accountId == null) return null;

        if (host.getId().equals(accountId)) {
            return host;
        }
        return getPlayer(accountId);
    }

    public boolean isHost(String accountId) {
        return host.getId().equals(accountId);
    }

    public boolean isRoomCode(String roomCode) {
        return this.roomCode.equals(roomCode);
    }

    public void updateRoomCode(){
        this.roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
