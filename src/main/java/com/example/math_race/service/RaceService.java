package com.example.math_race.service;

import com.example.math_race.dto.request.CreateRaceRequest;
import com.example.math_race.dto.request.JoinRaceRequest;
import com.example.math_race.dto.request.RaceInfoRequest;
import com.example.math_race.dto.request.RequestMetadata;
import com.example.math_race.dto.response.CreateRaceResponse;
import com.example.math_race.dto.response.JoinRaceResponse;
import com.example.math_race.dto.response.RaceInfoResponse;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.race.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
public class RaceService {

    @Getter
    private final Map<String, RaceManager> activeRaces = new ConcurrentHashMap<>();

    @Autowired
    private RaceValidator raceValidator;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public CreateRaceResponse creatRace(CreateRaceRequest request, RequestMetadata metadata){
        UserEntity user = authService.getActiveUserByToken(metadata.getAuthorization());

        if (user == null) {
            throw new LogicException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (findAccountById(user.getId()+"") != null) {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        String nickname = request.getNickname() != null && !request.getNickname().isEmpty() ?
                request.getNickname() : user.getUsername();


        RaceSettings raceSettings = new RaceSettings(request.getName(), request.getTargetScore());
        raceValidator.validate(raceSettings);

        RaceManager raceManager = new RaceManager(raceSettings);
        raceManager.setHost(new RaceHost(user.getId()+"",null,nickname));

        while (activeRaces.containsKey(raceManager.getRoomCode())){
            raceManager.updateRoomCode();
        }
        activeRaces.put(raceManager.getRoomCode(), raceManager);

        return new CreateRaceResponse(
                raceSettings.getRaceName(),
                raceManager.getRoomCode(),
                raceSettings.getTargetScore(),
                nickname
        );
    }

    public RaceInfoResponse raceInfo(RaceInfoRequest request, RequestMetadata metadata){
        UserEntity user = authService.getActiveUserByToken(metadata.getAuthorization());
        String accountId;

        if (user == null) {
            if (metadata.getGuestId() == null) {
                throw new LogicException(ErrorCode.INVALID_TOKEN);
            }
            accountId = metadata.getGuestId();
        }else {
            accountId = user.getId()+"";
        }

        RaceManager raceManager =  activeRaces.get(request.getRoomCode());

        if (raceManager == null)
            throw new LogicException(ErrorCode.RACE_NOT_FOUND);

        RaceAccount account = raceManager.getAccount(accountId);

        boolean isHost;
        if (account == null) {
            throw new LogicException(ErrorCode.USER_NOT_IN_RACE);
        }else {
            isHost = raceManager.isHost(accountId);
        }

        return new RaceInfoResponse(isHost,raceManager.getStatus().name(),account.getNickname(),
                raceManager.getSettings().getRaceName(),raceManager.getRoomCode(),raceManager.getSettings().getTargetScore());

    }

    public JoinRaceResponse joinRace(JoinRaceRequest request, RequestMetadata metadata){
        String accountId, nickname;
        UserEntity user = authService.getActiveUserByToken(metadata.getAuthorization());
        if (user == null) {
            if (metadata.getGuestId() != null) {
                accountId = metadata.getGuestId();
                nickname = request.getNickname() != null && !request.getNickname().isEmpty() ?
                        request.getNickname() : createNickname();
            }else {
                throw new LogicException(ErrorCode.INVALID_TOKEN);
            }
        } else {
            accountId =  user.getId()+"";
            nickname = request.getNickname() != null && !request.getNickname().isEmpty() ?
                    request.getNickname() : user.getUsername();
        }

        RaceManager raceManager = findRaceByAccountId(accountId);
        boolean isHost = false;

        if (raceManager == null){
            raceManager = activeRaces.get(request.getRoomCode());
            if (raceManager == null){
                throw new LogicException(ErrorCode.RACE_NOT_FOUND);
            }
            if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
                throw new LogicException(ErrorCode.RACE_ALREADY_STARTED);
            }

            raceManager.joinRace(new RacePlayer(accountId,null,nickname));

        } else if (raceManager.getRoomCode().equals(request.getRoomCode())){
            if (raceManager.getStatus().equals(RaceStatus.FINISHED)){
                throw new LogicException(ErrorCode.RACE_ALREADY_FINISHED);
            }

            if (raceManager.getHost().getId().equals(accountId)){
                isHost = true;
                raceManager.setHost(new RaceHost(accountId,null,nickname));
            }else {
                raceManager.joinRace(new RacePlayer(accountId, null, nickname));
            }
        }else {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        return new JoinRaceResponse(nickname,raceManager.getSettings().getRaceName(),
                raceManager.getRoomCode(),isHost ? "HOST" : "PLAYER",raceManager.getSettings().getTargetScore());
    }

    public RaceAccount findAccountById(String accountId) {
        if (accountId == null) return null;

        return activeRaces.values().stream()
                .map(race -> {

                    if (race.getHost().getId().equals(accountId)) {
                        return race.getHost();
                    }

                    return race.getPlayer(accountId);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public RaceManager findRaceByAccountId(String accountId) {
        if (accountId == null) return null;

        return activeRaces.values().stream()
                .filter(race ->
                        Objects.equals(race.getHost().getId(), accountId) ||
                                race.getPlayer(accountId) != null
                )
                .findFirst()
                .orElse(null);
    }

    public RaceManager findRaceByRoomCode(String roomCode) {
        return activeRaces.get(roomCode);
    }

    public String createNickname(){
        return "player " + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}