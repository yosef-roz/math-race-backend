package com.example.math_race.config.websocket;

import com.example.math_race.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(String sessionId) {
        WebSocketSession session = sessions.remove(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                // it's ok
            }
        }
    }

    public void forceDisconnect(String sessionId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close(new CloseStatus(4000, "Disconnected by newer connection"));
            } catch (IOException e) {
              //it's ok
            }
        }
    }

    public void forceDisconnect(String sessionId, ErrorCode errorCode) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {

                String reason = errorCode.name();
                session.close(new CloseStatus(4000, reason));
            } catch (IOException e) {
                // it's ok
            }
        }
    }
}