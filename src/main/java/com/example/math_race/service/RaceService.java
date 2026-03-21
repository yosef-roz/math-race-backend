package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.WsMessage;
import com.example.math_race.dto.wsMessage.response.PlayerJoinedDTO;
import com.example.math_race.dto.wsMessage.response.RaceStateDTO;
import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.*;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.race.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.example.math_race.service.WebSocketService.*;

@Service
@Transactional(readOnly = true)
public class RaceService {

    private final RaceValidator raceValidator;
    private final AuthService authService;
    private final WebSocketService webSocketService;
    private final RaceEngineService raceEngineService;
    private final Map<String, RaceManager> allRaces;
    private final Map<String, String> accountIdToOpenRoomCode;

    @Autowired
    public RaceService(RaceValidator raceValidator, AuthService authService, WebSocketService webSocketService, RaceEngineService raceEngineService) {
        this.raceValidator = raceValidator;
        this.authService = authService;
        this.webSocketService = webSocketService;
        this.raceEngineService = raceEngineService;
        this.allRaces = new ConcurrentHashMap<>();
        this.accountIdToOpenRoomCode = new ConcurrentHashMap<>();
    }

    public CreateRaceResponse creatRace(CreateRaceRequest request, RequestMetadata metadata){
        UserEntity user = authService.getActiveUserByToken(metadata.getAuthorization());

        if (user == null) {
            throw new LogicException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (findAccountByIdInOpenRace(user.getId()+"") != null) {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        String nickname = request.getNickname() != null && !request.getNickname().isEmpty() ?
                request.getNickname() : user.getUsername();


        RaceSettings raceSettings = new RaceSettings(request.getName(), request.getTargetScore());
        raceValidator.validate(raceSettings);

        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        RaceManager raceManager = new RaceManager(raceSettings);
        raceManager.setHost(new RaceHost(user.getId()+"",null,joinToken,nickname));

        while (allRaces.containsKey(raceManager.getRoomCode())){
            raceManager.updateRoomCode();
        }
        accountIdToOpenRoomCode.put(user.getId()+"", raceManager.getRoomCode());
        allRaces.put(raceManager.getRoomCode(), raceManager);

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

        RaceManager raceManager =  findOpenRaceByRoomCode(request.getRoomCode());

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

        RaceManager raceManager = findOpenRaceByAccountId(accountId);
        boolean isHost = false;
        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        if (raceManager == null){
            raceManager = findRaceByRoomCode(request.getRoomCode());
            if (raceManager == null || raceManager.getStatus().isClosed()){
                throw new LogicException(ErrorCode.RACE_NOT_FOUND);
            }
            if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
                throw new LogicException(ErrorCode.RACE_ALREADY_STARTED);
            }

            raceManager.joinRace(new RacePlayer(accountId,null,joinToken,nickname));
            accountIdToOpenRoomCode.put(accountId, raceManager.getRoomCode());

        } else if (raceManager.getRoomCode().equals(request.getRoomCode())){

            RaceAccount account = raceManager.getAccount(accountId);

            account.setNickname(nickname);
            account.setJoinToken(joinToken);
            isHost = raceManager.isHost(accountId);

        }else {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        if (!isHost) {
            sendPlayerJoined(raceManager, accountId);
        }

        return new JoinRaceResponse(nickname,joinToken,raceManager.getSettings().getRaceName(),
                raceManager.getRoomCode(),isHost ? "HOST" : "PLAYER",raceManager.getSettings().getTargetScore());
    }

    public void handleStartRace(String roomCode, StompHeaderAccessor accessor){
        RaceManager raceManager = findOpenRaceByRoomCode(roomCode);
        if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
            webSocketService.sendErrorToQueueSession(QUEUE_RACE_HOST,ErrorCode.RACE_ALREADY_INITIALIZED,accessor);
        }

        raceEngineService.startRace(raceManager);
    }

    public void sendRaceState(String roomCode, StompHeaderAccessor accessor){
        RaceManager raceManager = findRaceByRoomCode(roomCode);

        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,"RACE_FULL_STATE",
                new RaceStateDTO(raceManager),accessor);
    }

    public void sendPlayerJoined(RaceManager raceManager, String accountId){
        webSocketService.sendToQueueSession(QUEUE_RACE_HOST,
                WsMessage.success("PLAYER_JOINED",new PlayerJoinedDTO(raceManager.getPlayer(accountId))),
                raceManager.getHost().getId(),raceManager.getHost().getSessionActive());
    }

    public RaceAccount findAccountByIdInOpenRace(String accountId) {
        if (accountId == null) return null;

        String roomCode = accountIdToOpenRoomCode.get(accountId);
        if (roomCode == null) return null;

        RaceManager race = allRaces.get(roomCode);

        if (race != null && race.getStatus().isOpen()) {
            return race.getAccount(accountId);
        }

        return null;
    }

    public List<RaceAccount> findAccountByIdInClosedRace(String accountId) {
        if (accountId == null) return Collections.emptyList();

        return allRaces.values().stream()
                .filter(race -> race.getStatus().isClosed())
                .map(race -> race.getAccount(accountId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<RaceAccount> findAccountById(String accountId) {
        if (accountId == null) return Collections.emptyList();

        return allRaces.values().stream()
                .map(race -> race.getAccount(accountId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public RaceManager findRaceByAccountId(String accountId) {
        if (accountId == null) return null;

        return allRaces.values().stream()
                .filter(race ->
                        Objects.equals(race.getHost().getId(), accountId) ||
                                race.getPlayer(accountId) != null
                )
                .findFirst()
                .orElse(null);
    }

    public RaceManager findOpenRaceByAccountId(String accountId) {
        if (accountId == null) return null;

        String roomCode = accountIdToOpenRoomCode.get(accountId);
        if (roomCode == null) return null;

        RaceManager race = allRaces.get(roomCode);
        return (race != null && race.getStatus().isOpen()) ? race : null;
    }

    public List<RaceManager> findClosedRacesByAccountId(String accountId) {
        if (accountId == null) return Collections.emptyList();

        return allRaces.values().stream()
                .filter(race -> race.getStatus().isClosed())
                .filter(race -> race.isAccountIn(accountId))
                .collect(Collectors.toList());
    }

    public List<RaceManager> findAllRacesByAccountId(String accountId) {
        if (accountId == null) return Collections.emptyList();

        return allRaces.values().stream()
                .filter(race -> race.isAccountIn(accountId))
                .collect(Collectors.toList());
    }

    public RaceManager findOpenRaceByRoomCode(String roomCode) {
        RaceManager race = allRaces.get(roomCode);
        return (race != null && race.getStatus().isOpen()) ? race : null;
    }

    public RaceManager findClosedRaceByRoomCode(String roomCode) {
        RaceManager race = allRaces.get(roomCode);
        return (race != null && race.getStatus().isClosed()) ? race : null;
    }

    public RaceManager findRaceByRoomCode(String roomCode) {
        return allRaces.get(roomCode);
    }

    public String createNickname(){
        return "player " + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}