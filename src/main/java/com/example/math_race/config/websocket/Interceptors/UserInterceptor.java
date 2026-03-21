package com.example.math_race.config.websocket.Interceptors;

import com.example.math_race.config.websocket.WebSocketSessionRegistry;
import com.example.math_race.dto.wsMessage.WsMessage;
import com.example.math_race.dto.wsMessage.response.PlayerConnectionDTO;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.race.RaceAccount;
import com.example.math_race.race.RaceManager;
import com.example.math_race.service.AuthService;
import com.example.math_race.service.WebSocketService;
import com.example.math_race.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

import static com.example.math_race.service.WebSocketService.*;

@Component
public class UserInterceptor implements ChannelInterceptor {

    private final AuthService authService;
    private final WebSocketService webSocketService;
    private final RaceService raceService;
    private final WebSocketSessionRegistry webSocketSessionRegistry;
    private final AntPathMatcher matcher;

    @Autowired
    public UserInterceptor(AuthService authService, @Lazy WebSocketService webSocketService, @Lazy RaceService raceService, @Lazy WebSocketSessionRegistry webSocketSessionRegistry) {
        this.authService = authService;
        this.webSocketService = webSocketService;
        this.raceService = raceService;
        this.webSocketSessionRegistry = webSocketSessionRegistry;
        this.matcher = new AntPathMatcher();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            return handleSubscribe(message,accessor);
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
                throw new MessageDeliveryException(ErrorCode.AUTH_FAILED.name());
            }
            accessor.setUser(new UserPrincipal(String.valueOf(user.getId())));
        } else if (guestId != null && guestId.startsWith("Guest-") && guestId.length() == 16) {
            accessor.setUser(new UserPrincipal(guestId));
        } else {
            throw new MessageDeliveryException(ErrorCode.MISSING_IDENTIFICATION.name());
        }
    }

    private Message<?> handleSubscribe(Message<?> message, StompHeaderAccessor accessor) {
        UserPrincipal principal = (UserPrincipal) accessor.getUser();
        String destination = accessor.getDestination();
        String subId = accessor.getSubscriptionId();

        if (principal == null || destination == null) {
            throw new MessageDeliveryException(ErrorCode.USER_NOT_IDENTIFIED.name());
        }

        accessor.getSessionAttributes().put("sub_path_" + subId, destination);

        if(destination.contains("/race")) {
            RaceManager raceManager = raceService.findOpenRaceByAccountId(principal.getName());
            if (raceManager == null) {
                webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.USER_NOT_IN_ANY_RACE,accessor);
                return null;
            }

            if (destination.contains("/topic/race/")) {
                Map<String, String> vars = matcher.extractUriTemplateVariables("/topic/race/{roomCode}/updates", destination);
                if (!raceManager.isRoomCode(vars.get("roomCode"))) {
                    webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.NOT_REGISTERED_FOR_RACE,accessor);
                    return null;
                }
            } else if (destination.startsWith("/user/queue/race/feedback")) {
                if (raceManager.isHost(principal.getName())) {
                    webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.HOST_FORBIDDEN_PLAYER_ACTION,accessor);
                    return null;
                }
            } else if (destination.startsWith("/user/queue/race/host")) {
                if (!raceManager.isHost(principal.getName())) {
                    webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.NOT_RACE_HOST,accessor);
                    return null;
                }
            }

            RaceAccount account = raceManager.getAccount(principal.getName());
            String incomingToken = accessor.getFirstNativeHeader("Join-Token");

            if (!account.isConnected() || account.getSessionActive().equals(accessor.getSessionId()) ||
                    account.containsJoinToken() && account.getJoinToken().equals(incomingToken)) {

                if (account.isConnected() && !account.getSessionActive().equals(accessor.getSessionId())) {
                    webSocketSessionRegistry.forceDisconnect(account.getSessionActive(),ErrorCode.DUPLICATE_RACE_CONNECTION);
                }

                if (incomingToken != null && incomingToken.equals(account.getJoinToken())) {
                    account.setJoinToken(null);
                }

                account.setSessionActive(accessor.getSessionId());
                if (!destination.startsWith("/user/queue/race/host")) {
                    webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,"PLAYER_CONNECTION",
                            new PlayerConnectionDTO(principal.getName(), true), raceManager.getHost().getId(), raceManager.getHost().getSessionActive());
                }
            } else {
                webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.DUPLICATE_RACE_CONNECTION,accessor);
                return null;
            }

        }else if (!destination.equals("/user/queue/notifications")) {
            webSocketService.sendErrorToQueueSession(QUEUE_NOTIFICATIONS, ErrorCode.INVALID_RACE_PATH,accessor);
            return null;
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
            RaceManager raceManager = raceService.findOpenRaceByRoomCode(roomCode);

            if (raceManager == null) {
                webSocketService.sendToQueueSession(QUEUE_NOTIFICATIONS,
                       WsMessage.createError(ErrorCode.RACE_NOT_FOUND),accessor);

                return null;
            }

            RaceAccount account = raceManager.getAccount(user.getName());

            if (account == null) {
                webSocketService.sendToQueueSession(QUEUE_NOTIFICATIONS,
                        WsMessage.createError(ErrorCode.NOT_REGISTERED_FOR_RACE),accessor);

                return null;
            }

            boolean isHostPath = matcher.match("/app/race/{roomCode}/host/**", destination);
            if (isHostPath && !raceManager.isHost(account.getId())) {
                webSocketService.sendToQueueSession(QUEUE_NOTIFICATIONS,
                        WsMessage.createError(ErrorCode.NOT_RACE_HOST),accessor);

                return null;
            }

            boolean isPlayerPath = matcher.match("/app/race/{roomCode}/player/**", destination);
            if (isPlayerPath && raceManager.isHost(account.getId())) {
                webSocketService.sendToQueueSession(QUEUE_NOTIFICATIONS,
                        WsMessage.createError(ErrorCode.NOT_RACE_PLAYER),accessor);

                return null;
            }

            if (account.getSessionActive() != null && !account.getSessionActive().equals(accessor.getSessionId())) {
                webSocketService.sendToQueueSession(QUEUE_NOTIFICATIONS,
                        WsMessage.createError(ErrorCode.DUPLICATE_RACE_CONNECTION),accessor);

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
            webSocketService.addSession(principal.getName(), sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal != null && sessionId != null) {
            webSocketService.removeSession(principal.getName(), sessionId);

            RaceManager raceManager = raceService.findOpenRaceByAccountId(principal.getName());
            if (raceManager == null) return;
            RaceAccount account = raceManager.getAccount(principal.getName());

            if (account != null && sessionId.equals(account.getSessionActive())) {
                if (!raceManager.isHost(account.getId())) {
                    webSocketService.sendToQueueSession(QUEUE_RACE_HOST, WsMessage.success("PLAYER_CONNECTION",
                            new PlayerConnectionDTO(principal.getName(), false)), raceManager.getHost().getId(), raceManager.getHost().getSessionActive());
                }
                account.setSessionActive(null);
            }
        }
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();
        String subId = accessor.getSubscriptionId();
        String destination = (String) accessor.getSessionAttributes().get("sub_path_" + subId);

        if (principal != null && sessionId != null) {

            RaceManager raceManager = raceService.findOpenRaceByAccountId(principal.getName());
            if (raceManager == null) return;
            RaceAccount account =  raceManager.getAccount(principal.getName());

            if (account != null && sessionId.equals(account.getSessionActive())) {

                if (!raceManager.isHost(account.getId())) {
                    webSocketService.sendToQueueSession(QUEUE_RACE_HOST, WsMessage.success("PLAYER_CONNECTION",
                            new PlayerConnectionDTO(principal.getName(), false)), raceManager.getHost().getId(), raceManager.getHost().getSessionActive());
                }
                account.setSessionActive(null);
            }
        }
    }
}