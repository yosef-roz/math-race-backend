package com.example.math_race.controller;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.ApiResponse;
import com.example.math_race.dto.response.CreateRaceResponse;
import com.example.math_race.dto.response.JoinRaceResponse;
import com.example.math_race.dto.response.RaceInfoResponse;
import com.example.math_race.dto.wsMessage.request.*;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/race")
public class RaceController {

    private final RaceService raceService;

    @Autowired
    public  RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @MessageMapping("/race/{roomCode}/player/submit")
    public void handleSubmit(@DestinationVariable String roomCode, @Valid @Payload SubmitQuestionRequest request , StompHeaderAccessor accessor) {
        raceService.handleSubmitQuestion(roomCode,request, accessor);
    }

    @MessageMapping("/race/{roomCode}/player/junction/choose")
    public void handleJunctionChoose(@DestinationVariable String roomCode, @Valid @Payload JunctionChooseRequest request , StompHeaderAccessor accessor) {
        raceService.handleJunctionChoose(roomCode, request, accessor);
    }

    @MessageMapping("/race/{roomCode}/host/start")
    public void handleChangeRaceStatus(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.handleStartRace(roomCode, accessor);
    }

    @MessageMapping("/race/{roomCode}/host/kick")
    public void handleKickPlayer(@DestinationVariable String roomCode, @Valid @Payload KickPlayerRequest request, StompHeaderAccessor accessor) {
        raceService.kickPlayerFromRace(roomCode,request);
    }

    @MessageMapping("/race/{roomCode}/player/left")
    public void handlePlayerLeft(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.playerLeftFromRace(roomCode,accessor);
    }

    @MessageMapping({"/race/{roomCode}/player/change-nickname", "/race/{roomCode}/host/change-nickname"})
    public void handleChangeNickname(@DestinationVariable String roomCode, @Valid @Payload ChangeNicknameRequest request, StompHeaderAccessor accessor) {
        raceService.changeNickname(roomCode,request,accessor);
    }

    @MessageMapping("/race/{roomCode}/host/message-to-player")
    public void handleSendMessageToPlayer(@DestinationVariable String roomCode, @Valid @Payload MessageToPlayerRequest request , StompHeaderAccessor accessor) {
        raceService.sendMessageToPlayer(roomCode,request);
    }

    @MessageMapping("/race/{roomCode}/host/pause")
    public void handlePauseRace(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.pauseRace(roomCode);
    }

    @MessageMapping("/race/{roomCode}/host/resume")
    public void handleResumeRace(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.resumeRace(roomCode);
    }

    @MessageMapping("/race/{roomCode}/host/cancel")
    public void handleCancelRace(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.cancelRace(roomCode);
    }

    @MessageMapping({"/race/{roomCode}/host/sync", "/race/{roomCode}/player/sync"})
    public void handleRaceSync(@DestinationVariable String roomCode, StompHeaderAccessor accessor){
        raceService.sendRaceState(roomCode, accessor);
    }

    // http://localhost:8085/api/race/create
    @PostMapping("/create")
    public ApiResponse<CreateRaceResponse> createRace(@Valid @RequestBody CreateRaceRequest request, RequestMetadata metadata) {
        CreateRaceResponse createRaceResponse = raceService.creatRace(request, metadata);
        return ApiResponse.success(createRaceResponse);
    }

   @PostMapping("/{roomCode}/join")
    public ApiResponse<JoinRaceResponse> joinRace(@PathVariable String roomCode, @Valid @RequestBody JoinRaceRequest request, RequestMetadata metadata){
       if (!StringUtils.hasText(roomCode)) {
           return ApiResponse.error(ErrorCode.INVALID_INPUT);
       }

        JoinRaceResponse joinRaceResponse = raceService.joinRace(roomCode,request,metadata);
        return ApiResponse.success(joinRaceResponse);
    }

    @GetMapping("/{roomCode}/info")
    public ApiResponse<RaceInfoResponse> getRaceInfo(@PathVariable String roomCode, RequestMetadata metadata){
        if (!StringUtils.hasText(roomCode)) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        RaceInfoResponse raceInfoResponse = raceService.raceInfo(roomCode,metadata);
        return ApiResponse.success(raceInfoResponse);
    }
}
