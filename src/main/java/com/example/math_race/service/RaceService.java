package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.ChangeRaceStatusDTO;
import com.example.math_race.dto.wsMessage.WsMessage;
import com.example.math_race.dto.wsMessage.PlayerJoinedDTO;
import com.example.math_race.dto.wsMessage.RaceStateDTO;
import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.*;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.race.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.math_race.service.WebSocketService.*;

@Service
@Transactional(readOnly = true)
public class RaceService {

    private final RaceValidator raceValidator;
    private final AuthService authService;
    private final WebSocketService webSocketService;
    private final RaceEngineService raceEngineService;

    @Getter
    private final Map<String, RaceManager> activeRaces;

    @Autowired
    public RaceService(RaceValidator raceValidator, AuthService authService, WebSocketService webSocketService, RaceEngineService raceEngineService) {
        this.raceValidator = raceValidator;
        this.authService = authService;
        this.webSocketService = webSocketService;
        this.raceEngineService = raceEngineService;
        this.activeRaces = new ConcurrentHashMap<>();

    }

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

        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        RaceManager raceManager = new RaceManager(raceSettings);
        raceManager.setHost(new RaceHost(user.getId()+"",null,joinToken,nickname));

        while (activeRaces.containsKey(raceManager.getRoomCode())){
            raceManager.updateRoomCode();
        }
        activeRaces.put(raceManager.getRoomCode(), raceManager);

        return new CreateRaceResponse(
                raceSettings.getRaceName(),
                raceManager.getRoomCode(),
                raceSettings.getTargetScore(),
                nickname,
                joinToken
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

        if (account == null) {
            throw new LogicException(ErrorCode.NOT_REGISTERED_FOR_RACE);
        }

        return new RaceInfoResponse(account,raceManager);
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
        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        if (raceManager == null){
            raceManager = activeRaces.get(request.getRoomCode());
            if (raceManager == null){
                throw new LogicException(ErrorCode.RACE_NOT_FOUND);
            }
            if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
                throw new LogicException(ErrorCode.RACE_ALREADY_STARTED);
            }

            raceManager.joinRace(new RacePlayer(accountId,null,joinToken,nickname));

        } else if (raceManager.getRoomCode().equals(request.getRoomCode())){
            if (raceManager.getStatus().equals(RaceStatus.FINISHED)){
                throw new LogicException(ErrorCode.RACE_ALREADY_FINISHED);
            }

            RaceAccount account = raceManager.getAccount(accountId);
//            if (account.isConnected()){
//                webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS,ErrorCode.DUPLICATE_RACE_CONNECTION,
//                        accountId,account.getSessionActive());
//            }

            account.setNickname(nickname);
            account.setJoinToken(joinToken);
            if (raceManager.isHost(accountId)){
                isHost = true;
            }

        }else {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        if (!isHost) {
            sendPlayerJoined(raceManager, accountId);
        }

        return new JoinRaceResponse(nickname,joinToken,raceManager.getSettings().getRaceName(),
                raceManager.getRoomCode(),isHost ? "HOST" : "PLAYER",raceManager.getSettings().getTargetScore());
    }

    public void handleChangeRaceStatus(String roomCode, ChangeRaceStatusDTO request, StompHeaderAccessor accessor){
        RaceManager raceManager = activeRaces.get(roomCode);
        boolean succeeded = false;
        String sendType = "STATUS_CHANGED";

        if (raceManager.getStatus().equals(RaceStatus.PENDING)){
            if (request.getStatus().equals(RaceStatus.IN_PROGRESS.name()) ||
                    request.getStatus().equals(RaceStatus.CANCELLED.name())){
                succeeded = true;
            }

        } else if (raceManager.getStatus().equals(RaceStatus.IN_PROGRESS)){
            if (request.getStatus().equals(RaceStatus.PAUSED.name()) ||
                    request.getStatus().equals(RaceStatus.CANCELLED.name())){
                succeeded = true;
            }

        } else if (raceManager.getStatus().equals(RaceStatus.PAUSED)) {
            if (request.getStatus().equals(RaceStatus.IN_PROGRESS.name())||
                    request.getStatus().equals(RaceStatus.CANCELLED.name())){
                succeeded = true;
            }
        }

        if (succeeded){
            webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(roomCode),sendType,
                    new ChangeRaceStatusDTO(request.getStatus()));
            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,sendType,
                    new ChangeRaceStatusDTO(request.getStatus()),raceManager.getHost().getId(),raceManager.getHost().getSessionActive());
        }else {

            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,sendType,
                    new ChangeRaceStatusDTO(request.getStatus()),raceManager.getHost().getId(),raceManager.getHost().getSessionActive());
        }
    }

    public void sendRaceState(String roomCode, StompHeaderAccessor accessor){
        RaceManager raceManager = activeRaces.get(roomCode);

        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,"RACE_FULL_STATE",
                new RaceStateDTO(raceManager),accessor);
    }

    public void sendPlayerJoined(RaceManager raceManager, String accountId){
        webSocketService.sendToQueueSession(QUEUE_RACE_HOST,
                WsMessage.success("PLAYER_JOINED",new PlayerJoinedDTO(raceManager.getPlayer(accountId))),
                raceManager.getHost().getId(),raceManager.getHost().getSessionActive());
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