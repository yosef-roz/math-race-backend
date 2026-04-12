package com.example.math_race.controller;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.ApiResponse;
import com.example.math_race.dto.response.CreateRaceResponse;
import com.example.math_race.dto.response.JoinRaceResponse;
import com.example.math_race.dto.response.RaceInfoResponse;
import com.example.math_race.dto.wsMessage.request.SubmitQuestionRequest;
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

    @MessageMapping("/race/{roomCode}/host/start")
    public void handleChangeRaceStatus(@DestinationVariable String roomCode, StompHeaderAccessor accessor) {
        raceService.handleStartRace(roomCode, accessor);
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
