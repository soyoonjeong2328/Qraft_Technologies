package org.example.qraft_technologies.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveClientManager {
    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    public synchronized boolean register(String token, WebSocketSession session) {
        if(clients.containsKey(token)) {
            return false;
        }
        clients.put(token, session);
        return true;
    }

    public synchronized void unregister(String token) {
        clients.remove(token);
    }

    public WebSocketSession getSession(String token) {
        return clients.get(token);
    }
}
