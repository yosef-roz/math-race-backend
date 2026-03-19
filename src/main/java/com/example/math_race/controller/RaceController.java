package com.example.math_race.controller;

import com.example.math_race.dto.request.*;
import com.example.math_race.dto.response.ApiResponse;
import com.example.math_race.dto.response.CreateRaceResponse;
import com.example.math_race.dto.response.JoinRaceResponse;
import com.example.math_race.dto.response.RaceInfoResponse;
import com.example.math_race.dto.wsMessage.ChangeRaceStatusDTO;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/race")
public class RaceController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RaceService raceService;

    @Autowired
    public  RaceController(RaceService raceService, SimpMessagingTemplate messagingTemplate) {
        this.raceService = raceService;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/race/{roomCode}/join9")
    public void handleJoin(@DestinationVariable String roomCode, @Payload Object request,
                           Principal principal,
                           SimpMessageHeaderAccessor headerAccessor) {

        // 1. חילוץ המזהים
        String userId = principal.getName(); // ה-ID של החשבון
        String sessionId = headerAccessor.getSessionId(); // ה-ID של המכשיר הספציפי

        System.out.println("User " + userId + " joined from session " + sessionId);

        // --- כאן תבוא הלוגיקה של הוספת השחקן לחדר ב-DB או בזיכרון ---

        // 2. יצירת Headers שמכוונים רק למכשיר הזה
        SimpMessageHeaderAccessor responseHeaders = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeaders.setSessionId(sessionId);
        responseHeaders.setLeaveMutable(true);

        // 3. שליחת הודעת אישור רק למכשיר שביצע את ה-Join
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/race-updates",
                "נכנסת למרוץ בהצלחה!",
                responseHeaders.getMessageHeaders()
        );
    }

    @MessageMapping("/race/{roomCode}/submit")
    @SendToUser("/queue/game-events") // גם זה חוזר לאותו צינור
    public void handleSubmit(@DestinationVariable String roomCode, @Payload Object answer, Principal principal) {

        // לוגיקה לבדיקת תשובה...
        boolean isCorrect = true;

        if (isCorrect) {
            // 2. שידור לכל מי שנמצא בחדר (כולל המורה והתלמידים האחרים)
            // כולם מאזינים לכתובת הזו ב-React
            String roomDestination = "/topic/race/" + roomCode;

            // יצירת אובייקט עדכון (למשל: "שמעון התקדם למיקום 50")
            Object broadcastEvent = null; //new GameEvent("PLAYER_MOVED", answer.getPlayerId() + " moved forward!");

            // השורה ששולחת לכולם:
            messagingTemplate.convertAndSend(roomDestination, broadcastEvent);

            // השרת שולח לערוץ משתמש פרטי
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/game-events",
                    answer
            );
        }

        // מחזירים אירוע מסוג תוצאת תשובה
        // return new Object("ANSWER_RESULT", isCorrect ? "צדקת!" : "טעית...");
    }


    @MessageMapping("/race/{roomCode}/host/c")
    public void handleChangeRaceStatus(@DestinationVariable String roomCode, ChangeRaceStatusDTO request, StompHeaderAccessor accessor) {

    }

    @MessageMapping("/race/{roomCode}/host/sync")
    public void handleRaceSync(@DestinationVariable String roomCode, StompHeaderAccessor accessor){
        raceService.sendRaceState(roomCode, accessor);
    }

    // http://localhost:8085/api/race/create
    @PostMapping("/create")
    public ApiResponse<CreateRaceResponse> createRace(@RequestBody CreateRaceRequest request, RequestMetadata metadata) {
        if (request == null || request.getTargetScore() == null || request.getTargetScore() <=0 ) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        CreateRaceResponse createRaceResponse = raceService.creatRace(request, metadata);
        return ApiResponse.success(createRaceResponse);
    }

   @PostMapping("/join")
    public ApiResponse<JoinRaceResponse> join(@RequestBody JoinRaceRequest request, RequestMetadata metadata){
        if (request == null || request.getRoomCode() == null || request.getRoomCode().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        JoinRaceResponse joinRaceResponse = raceService.joinRace(request,metadata);
        return ApiResponse.success(joinRaceResponse);
    }

    @PostMapping("/info")
    public ApiResponse<RaceInfoResponse> raceInfo(@RequestBody RaceInfoRequest request, RequestMetadata metadata){
        if (request == null || request.getRoomCode() == null || request.getRoomCode().isEmpty()) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        RaceInfoResponse raceInfoResponse = raceService.raceInfo(request,metadata);
        return ApiResponse.success(raceInfoResponse);
    }
}
