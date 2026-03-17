package com.example.math_race.config.Interceptors;

import com.example.math_race.dto.response.NotificationResponse;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.race.RaceAccount;
import com.example.math_race.race.RaceManager;
import com.example.math_race.service.AuthService;
import com.example.math_race.service.NotificationService;
import com.example.math_race.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.Map;

import static com.example.math_race.service.NotificationService.QUEUE_NOTIFICATIONS;
import static com.example.math_race.service.NotificationService.RACE_PATH_PATTERN;

@Component
public class UserInterceptor implements ChannelInterceptor {

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RaceService raceService;

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            handleSubscribe(accessor);
        } else if (StompCommand.MESSAGE.equals(command)) {
            return handleOutboundMessage(message, accessor);
        }else if (StompCommand.SEND.equals(accessor.getCommand())){
            return handleSend(message,accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String auth = accessor.getFirstNativeHeader("Authorization");
        String guestId = accessor.getFirstNativeHeader("GuestID");

        if (auth != null && auth.startsWith("Bearer ")) {
            UserEntity user = authService.getActiveUserByToken(auth.substring(7));
            if (user == null) {
                throw new MessageDeliveryException("AUTH_FAILED");
            }
            accessor.setUser(new UserPrincipal(String.valueOf(user.getId())));
        } else if (guestId != null && guestId.startsWith("Guest-") && guestId.length() == 16) {
            accessor.setUser(new UserPrincipal(guestId));
        } else {
            throw new MessageDeliveryException("AUTH_REQUIRED");
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        UserPrincipal principal = (UserPrincipal) accessor.getUser();
        String destination = accessor.getDestination();
        if (principal == null || destination == null) throw new MessageDeliveryException("IDENTITY_ERROR");

        if(destination.contains("/race")) {
            RaceManager raceManager = raceService.findRaceByAccountId(principal.getName());
            if (raceManager == null) throw new MessageDeliveryException("RACE_NOT_FOUND");

            if (destination.contains("/topic/race/")) {
                Map<String, String> vars = matcher.extractUriTemplateVariables("/topic/race/{roomCode}/updates", destination);
                if (!raceManager.isRoomCode(vars.get("roomCode"))) {
                    throw new MessageDeliveryException("ACCESS_DENIED");
                }
            } else if (destination.startsWith("/user/queue/race/feedback")) {
                if (raceManager.isHost(principal.getName())) {
                    throw new MessageDeliveryException("ACCESS_DENIED");
                }
            } else if (destination.startsWith("/user/queue/race/host")) {
                if (!raceManager.isHost(principal.getName())) {
                    throw new MessageDeliveryException("ACCESS_DENIED");
                }
            }

            RaceAccount account = raceManager.getAccount(principal.getName());
            if (account == null) throw new MessageDeliveryException("IDENTITY_ERROR");

//            if (account.getSessionActive() != null && !account.getSessionActive().equals(accessor.getSessionId())) {
//                throw new MessageDeliveryException("SESSION_CONFLICT");
//            }else if (account.getSessionActive() == null){

            if (account.getSessionActive() == null || !account.getSessionActive().equals(accessor.getSessionId())){
                if (account.getSessionActive() != null){
                    notificationService.sendNotificationToQueueSession(QUEUE_NOTIFICATIONS,
                            NotificationResponse.createError(ErrorCode.SESSION_TRANSFERRED_TO_NEW_DEVICE),principal.getName(),account.getSessionActive());
                }
                account.setSessionActive(accessor.getSessionId());
            }

                // בסוף הגדרתי שאחרון שמצבע רישום המכשיר שלו נכנס
            // הצטרפות למירוץ מוסיפה רק את החשבון הזה למירוץ
          //  }

        }else if (!destination.equals("/user/queue/notifications")) {
            throw new MessageDeliveryException("ACCESS_DENIED");
        }
    }

    private Message<?> handleOutboundMessage(Message<?> message, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();
        String destination = accessor.getDestination();

        if (principal == null || destination == null || sessionId == null) {
            return null;
        }

        String accountId = principal.getName();

        if (!notificationService.isSessionExists(accountId, sessionId)) {
            return null;
        }

        if (destination.contains("/race/")) {
            RaceAccount account = raceService.findAccountById(accountId);

            if (account == null || account.getSessionActive() == null || !account.getSessionActive().equals(sessionId)) {
                return null;
            }
        }

        return message;
    }


    private Message<?> handleSend(Message<?> message, StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        Principal user = accessor.getUser();
        if (user == null || destination == null) return null;

        if (matcher.match(RACE_PATH_PATTERN, destination)){
            Map<String, String> vars = matcher.extractUriTemplateVariables(RACE_PATH_PATTERN, destination);
            String roomCode = vars.get("roomCode");
            RaceManager raceManager = raceService.findRaceByRoomCode(roomCode);

            if (raceManager == null) {
                notificationService.sendNotificationToQueueSession(QUEUE_NOTIFICATIONS,
                       NotificationResponse.createError(ErrorCode.RACE_NOT_FOUND),accessor);

                return null;
            }

            RaceAccount account = raceManager.getAccount(user.getName());

            if (account == null) {
                notificationService.sendNotificationToQueueSession(QUEUE_NOTIFICATIONS,
                        NotificationResponse.createError(ErrorCode.NOT_REGISTERED_FOR_RACE),accessor);

                return null;
            }

            if (account.getSessionActive() != null && !account.getSessionActive().equals(accessor.getSessionId())) {
                notificationService.sendNotificationToQueueSession(QUEUE_NOTIFICATIONS,
                        NotificationResponse.createError(ErrorCode.DUPLICATE_RACE_CONNECTION),accessor);

                return null;
            }

        }
        return message;
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal != null && sessionId != null) {
            notificationService.addSession(principal.getName(), sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal != null && sessionId != null) {
            notificationService.removeSession(principal.getName(), sessionId);

            RaceAccount account = raceService.findAccountById(principal.getName());

            if (account != null && sessionId.equals(account.getSessionActive())) {
                account.setSessionActive(null);
                // כאן אפשר להוסיף הודעת Broadcast לשאר החדר שהמשתמש התנתק
            }
        }
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal != null && sessionId != null) {

            RaceAccount account = raceService.findAccountById(principal.getName());
            if (account != null && sessionId.equals(account.getSessionActive())) {
                account.setSessionActive(null);
            }
//                // צריך להודיע לכל המשתתפים או למנהל שהUSER הזה יצא או כבר לא מחובר
//
        }
    }
}