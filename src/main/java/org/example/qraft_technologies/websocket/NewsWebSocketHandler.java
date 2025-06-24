package org.example.qraft_technologies.websocket;

import lombok.extern.slf4j.Slf4j;
import org.example.qraft_technologies.exception.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NewsWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public Set<WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String token = extractToken(session);
            session.getAttributes().put("token", token);

            sessions.add(session);
            log.info("WebSocket 연결 완료: token={}, sessionId={}", token, session.getId());
        } catch (InvalidTokenException e) {
            log.warn("WebSocket 연결 실패 - 세션 ID: {}, 이유: {}", session.getId(), e.getMessage());

            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        String token = extractToken(session);
        log.info("WebSocket 연결 종료: token={}, sessions={}, status={}", token, session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("웹소켓 연결 중 오류 발생 - 세션 ID: {}, 에러: {}", session.getId(), exception.getMessage());
    }

    private String extractToken(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            throw new InvalidTokenException("쿼리 파라미터가 존재하지 않아 토큰을 추출할 수 없습니다.");
        }

        String query = session.getUri().getQuery();
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("token")) {
                return keyValue[1];
            }
        }

        throw new InvalidTokenException("token 파라미터가 존재하지 않습니다.");
    }


}
