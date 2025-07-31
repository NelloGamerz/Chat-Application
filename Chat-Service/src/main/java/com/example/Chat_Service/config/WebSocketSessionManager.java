package com.example.Chat_Service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(String userId, WebSocketSession session){
        sessions.put(userId, session);
    }

    public void removeSession(String userId){
        sessions.remove(userId);
    }

    public WebSocketSession getSession(String userId){
        return sessions.get(userId);
    }

    public boolean containsSession(String userId){
        return sessions.containsKey(userId);
    }
}