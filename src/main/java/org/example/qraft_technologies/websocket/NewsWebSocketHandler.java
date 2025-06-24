package org.example.qraft_technologies.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class NewsWebSocketHandler extends TextWebSocketHandler {

    @Getter
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ActiveClientManager manager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String token = extractToken(session);
            session.getAttributes().put("token", token);

            if(!manager.register(token, session)) {
                log.warn("중복 연결 시도 차단: token={}, sessionId={}", token, session.getId());
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

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
        String token = (String) session.getAttributes().get("token");

        if(token != null) {
            manager.unregister(token);
        }
        log.info("WebSocket 연결 종료: token={}, sessions={}, status={}", token, session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("웹소켓 연결 중 오류 발생 - 세션 ID: {}, 에러: {}", session.getId(), exception.getMessage());
    }

    private String extractToken(WebSocketSession session) {
        String header = session.getHandshakeHeaders().getFirst("Authorization");

        if(header == null || !header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }

        return header.substring(7);
    }


}
