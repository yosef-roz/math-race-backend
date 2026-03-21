package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.WsMessage;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    public static final String QUEUE_NOTIFICATIONS = "/queue/notifications";
    public static final String QUEUE_RACE_FEEDBACK = "/queue/race/feedback";
    public static final String QUEUE_RACE_HOST = "/queue/race/host";
    public static final String TOPIC_RACE_PREFIX = "/topic/race/";
    public static final String RACE_PATH_PATTERN = "/app/race/{roomCode}/**";

    @Getter
    private final Map<String, Set<String>> userSessions;
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.userSessions = new ConcurrentHashMap<>();

    }

    public void sendToQueue(String path, WsMessage<?> wsMessage, StompHeaderAccessor accessor) {
       sendToQueue(path, wsMessage,accessor.getUser().getName());
    }

    public void sendToQueueSession(String path, WsMessage<?> wsMessage, StompHeaderAccessor accessor) {
        sendToQueueSession(path, wsMessage,accessor.getUser().getName(),accessor.getSessionId());
    }

    public void sendErrorToQueueSession(String path, ErrorCode errorCode, StompHeaderAccessor accessor) {
        sendToQueueSession(path, WsMessage.createError(errorCode),accessor.getUser().getName(),accessor.getSessionId());
    }

    public void sendErrorToQueueSession(String path, ErrorCode errorCode, String userId, String sessionId) {
        sendToQueueSession(path, WsMessage.createError(errorCode),userId,sessionId);
    }

    public <T> void sendSuccessToQueueSession(String path, String type, T data , StompHeaderAccessor accessor) {
        sendToQueueSession(path, WsMessage.success(type,data),accessor.getUser().getName(),accessor.getSessionId());
    }

    public <T> void sendSuccessToQueueSession(String path, String type, T data, String userId, String sessionId) {
        sendToQueueSession(path, WsMessage.success(type,data),userId,sessionId);
    }


    public void sendToQueueSession(String path, WsMessage<?> wsMessage, String userId, String sessionId) {
        if (sessionId == null) {
            return;
        }

        SimpMessageHeaderAccessor responseHeaders = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeaders.setSessionId(sessionId);
        responseHeaders.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(
                userId,
                path,
                wsMessage,
                responseHeaders.getMessageHeaders()
        );
    }

    public void sendToQueue(String path, WsMessage<?> wsMessage, String userId) {
        messagingTemplate.convertAndSendToUser(userId, path, wsMessage);
    }

    public void sendToTopic(String path, WsMessage<?> wsMessage) {
        messagingTemplate.convertAndSend(path, wsMessage);
    }

    public <T> void sendSuccessToTopic(String path, String type, T data) {
        sendToTopic(path, WsMessage.success(type,data));
    }

    public boolean isSessionExists(String userId, String sessionId) {
        return userSessions.getOrDefault(userId, Set.of()).contains(sessionId);
    }


    public void addSession(String userId, String sessionId) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
    }

    public void removeSession(String userId, String sessionId) {
        // כדאי לשלוח הודעה למשתמש
        Set<String> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public String getRaceUpdatesTopic(String roomCode) {
        return TOPIC_RACE_PREFIX + roomCode + "/updates";
    }

    @MessageExceptionHandler(LogicException.class)
    @SendToUser("queue/notifications") // שולח רק למשתמש שעשה את השגיאה
    public void handleLogicException(LogicException ex) {

    }
}