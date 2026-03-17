package com.example.math_race.service;

import com.example.math_race.config.Interceptors.UserPrincipal;
import com.example.math_race.dto.response.NotificationResponse;
import com.example.math_race.entities.UserEntity;
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

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Getter
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    public static final String QUEUE_NOTIFICATIONS = "/queue/notifications";
    public static final String QUEUE_RACE_FEEDBACK = "/queue/race/feedback";
    public static final String QUEUE_RACE_HOST = "/queue/race/host";
    public static final String TOPIC_RACE_PREFIX = "/topic/race/";
    public static final String RACE_PATH_PATTERN = "/app/race/{roomCode}/**";


    public void sendNotificationToQueue(String path, NotificationResponse<?> notificationResponse, StompHeaderAccessor accessor) {
       sendNotificationToQueueSession(path,notificationResponse,accessor.getUser().getName(),null);
    }

    public void sendNotificationToQueueSession(String path, NotificationResponse<?> notificationResponse, StompHeaderAccessor accessor) {
        sendNotificationToQueueSession(path,notificationResponse,accessor.getUser().getName(),accessor.getSessionId());
    }

    public void sendNotificationToQueueSession(String path, NotificationResponse<?> notificationResponse, String userId, String sessionId) {
        if (sessionId == null) {
            messagingTemplate.convertAndSendToUser(userId, path, notificationResponse);
            return;
        }

        SimpMessageHeaderAccessor responseHeaders = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeaders.setSessionId(sessionId);
        responseHeaders.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(
                userId,
                path,
                notificationResponse,
                responseHeaders.getMessageHeaders()
        );
    }

    public void sendNotificationToTopic(String path, NotificationResponse<?> notificationResponse) {
        messagingTemplate.convertAndSend(path, notificationResponse);
    }



//    public void sendGeneralNotification(UserEntity user, String message) {
//       sendGeneralNotification(user.getId()+"",NotificationResponse.generalMessage(message));
//    }
//
//    public void  sendGeneralNotification(String usedId, NotificationResponse<?> notificationResponse) {
//        messagingTemplate.convertAndSendToUser(usedId,QUEUE_NOTIFICATIONS,notificationResponse);
//    }

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

    @MessageExceptionHandler(LogicException.class)
    @SendToUser("queue/notifications") // שולח רק למשתמש שעשה את השגיאה
    public void handleLogicException(LogicException ex) {

    }
}