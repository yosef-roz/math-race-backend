package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.request.SubmitQuestionRequest;
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
import org.springframework.util.StringUtils;

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
        UserEntity user = authService.getValidUser(metadata);

        if (findAccountByIdInOpenRace(user.getId().toString()) != null) {
            throw new LogicException(ErrorCode.USER_ALREADY_IN_RACE);
        }

        String nickname = request.getNickname() != null && !request.getNickname().isEmpty() ?
                request.getNickname() : user.getUsername();


        RaceSettings raceSettings = new RaceSettings(request.getName(), request.getTargetScore());
        raceValidator.validate(raceSettings);

        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        RaceManager raceManager = new RaceManager(raceSettings);
        raceManager.setHost(new RaceHost(user,null,joinToken,nickname));

        while (allRaces.containsKey(raceManager.getRoomCode())){
            raceManager.updateRoomCode();
        }
        accountIdToOpenRoomCode.put(user.getId().toString(), raceManager.getRoomCode());
        allRaces.put(raceManager.getRoomCode(), raceManager);


        fullRace(false,raceManager);

        return new CreateRaceResponse(
                raceSettings.getRaceName(),
                raceManager.getRoomCode(),
                raceSettings.getTargetScore(),
                nickname,
                joinToken
        );
    }

    public void fullRace(boolean toDo, RaceManager race){
        if (toDo){
            for (int i = 0; i < 19; i++) {
                race.joinRace(new RacePlayer("stam" + UUID.randomUUID().toString().substring(0, 8), "", "", createNickname()));
            }
        }

    }

    public RaceInfoResponse raceInfo(String roomCode, RequestMetadata metadata){
        UserEntity user = authService.getActiveUserByToken(metadata);
        String accountId;

        if (user == null) {
            if (!StringUtils.hasText(metadata.getGuestId())) {
                throw new LogicException(ErrorCode.INVALID_TOKEN);
            }
            accountId = metadata.getGuestId();
        }else {
            accountId = user.getId().toString();
        }

        RaceManager raceManager =  findOpenRaceByRoomCode(roomCode);

        if (raceManager == null)
            throw new LogicException(ErrorCode.RACE_NOT_FOUND);

        RaceAccount account = raceManager.getAccount(accountId);

        if (account == null) {
            throw new LogicException(ErrorCode.NOT_REGISTERED_FOR_RACE);
        }

        return new RaceInfoResponse(account,raceManager);
    }

    public JoinRaceResponse joinRace(String roomCode, JoinRaceRequest request, RequestMetadata metadata){
        String accountId, nickname;
        UserEntity user = authService.getActiveUserByToken(metadata);

        if (user == null) {
            if (StringUtils.hasText(metadata.getGuestId()) && authService.isValidGuestId(metadata.getGuestId())) {
                accountId = metadata.getGuestId();
                nickname = StringUtils.hasText(request.getNickname()) ?
                        request.getNickname() : createNickname();
            }else {
                throw new LogicException(ErrorCode.INVALID_TOKEN);
            }
        } else {
            accountId =  user.getId().toString();
            nickname = StringUtils.hasText(request.getNickname()) ?
                    request.getNickname() : user.getUsername();
        }

        RaceManager raceManager = findOpenRaceByAccountId(accountId);
        boolean isHost = false;
        String joinToken = UUID.randomUUID().toString().substring(0, 10);

        if (raceManager == null){
            raceManager = findRaceByRoomCode(roomCode);
            if (raceManager == null || raceManager.getStatus().isClosed()){
                throw new LogicException(ErrorCode.RACE_NOT_FOUND);
            }
            if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
                throw new LogicException(ErrorCode.RACE_ALREADY_STARTED);
            }

            raceManager.joinRace(new RacePlayer(accountId,user,null,joinToken,nickname));
            accountIdToOpenRoomCode.put(accountId, raceManager.getRoomCode());

        } else if (raceManager.getRoomCode().equals(roomCode)){

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


    public void handleSubmitQuestion(String roomCode, SubmitQuestionRequest request, StompHeaderAccessor accessor){
        RaceManager race = findOpenRaceByRoomCode(roomCode);
        if (race.getStatus().isRunning()){
            raceEngineService.processPlayerAnswer(race,race.getPlayer(accessor.getUser().getName()),request.getAnswer());
        }
    }

    public void handleStartRace(String roomCode, StompHeaderAccessor accessor){
        RaceManager raceManager = findOpenRaceByRoomCode(roomCode);
        if (!raceManager.getStatus().equals(RaceStatus.PENDING)){
            webSocketService.sendErrorToQueueSession(QUEUE_RACE_HOST,ErrorCode.RACE_ALREADY_INITIALIZED,accessor);
        }

        if (raceManager.getPlayers().size() < 0){
            webSocketService.sendErrorToQueueSession(QUEUE_RACE_HOST,ErrorCode.NOT_ENOUGH_PLAYERS_TO_START,accessor);
        }

        raceEngineService.startRace(raceManager);
    }

    public void sendRaceState(String roomCode, StompHeaderAccessor accessor){
        RaceManager raceManager = findRaceByRoomCode(roomCode);
        boolean isHost = raceManager.isHost(accessor.getUser().getName());


        webSocketService.sendSuccessToQueueSession(isHost ? QUEUE_RACE_HOST : QUEUE_RACE_FEEDBACK, "RACE_FULL_STATE",
                new RaceStateDTO(raceManager, raceManager.getAccount(accessor.getUser().getName())),accessor);
    }

    public void sendPlayerJoined(RaceManager race, String accountId){
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,
               "PLAYER_JOINED",new PlayerJoinedDTO(race,race.getPlayer(accountId),true),
                race.getHost().getId(),race.getHost().getSessionActive());

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()),
                "PLAYER_JOINED",new PlayerJoinedDTO(race,race.getPlayer(accountId),false));
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