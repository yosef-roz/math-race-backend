package com.example.math_race.race;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RaceManager {

    private String id;

    public static final int MAX_PLAYERS = 20;

    private final RaceSettings settings;
    private String roomCode;

    private RaceHost host;
    private final Map<String, RacePlayer> players;

    private RaceStatus status;
    private long remainingTimeMillis;     // כמה זמן יש לשמשחק
    private long lastResumedAtMillis;// חותמת הזמן של הפעלת השעון האחרונה
    private final long createdAtMillis;
    private long endedAtMillis;

    public RaceManager(RaceSettings raceSettings) {
        this.id = UUID.randomUUID().toString();
        this.status = RaceStatus.PENDING;

        this.settings = raceSettings;
        updateRoomCode();

        this.players = new ConcurrentHashMap<>();

        this.remainingTimeMillis = raceSettings.getTotalDurationMillis();
        this.lastResumedAtMillis = 0;
        this.createdAtMillis = System.currentTimeMillis();
    }

    public void joinRace(RacePlayer player) {
        if (players.size() >= MAX_PLAYERS && !players.containsKey(player.getId())) {
            throw new LogicException(ErrorCode.RACE_MAX_PLAYERS_EXCEEDED);
        }

        RacePlayer existingPlayer = players.get(player.getId());

        if (existingPlayer != null) {
            existingPlayer.setSessionActive(player.getSessionActive());
            existingPlayer.setJoinToken(player.getJoinToken());
            existingPlayer.setNickname(player.getNickname());
        } else {
            players.put(player.getId(), player);
        }
    }

    public RacePlayer getPlayer(String playerId) {
        return players.get(playerId);
    }

    public RaceAccount getAccount(String accountId) {
        if (accountId == null) return null;

        if (isHost(accountId)) {
            return host;
        }
        return getPlayer(accountId);
    }

    public long getCalculatedRemainingTime() {
        if (this.status != RaceStatus.IN_PROGRESS) {
            return this.remainingTimeMillis;
        }

        long timeElapsedSinceResume = System.currentTimeMillis() - this.lastResumedAtMillis;
        long actualRemaining = this.remainingTimeMillis - timeElapsedSinceResume;

        return Math.max(0, actualRemaining);
    }

    public boolean isAccountIn(String accountId) {
        if (accountId == null) return false;
        return players.containsKey(accountId) || isHost(accountId);
    }

    public boolean isHost(String accountId) {
        return host != null && Objects.equals(host.getId(), accountId);
    }

    public boolean isRoomCode(String roomCode) {
        return this.roomCode.equals(roomCode);
    }

    public void updateRoomCode(){
        this.roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
