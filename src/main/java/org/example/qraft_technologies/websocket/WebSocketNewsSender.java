package org.example.qraft_technologies.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.dto.NewsDto;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.exception.WebSocketSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketNewsSender extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketNewsSender.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void broadcast(TranslatedNews translatedNews) {
        try {
            String json = objectMapper.writeValueAsString(NewsDto.from(translatedNews));
            logger.info("[WebSocket] 뉴스 전송 시작 : {}", translatedNews.getId());

            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                    logger.info("[WebSocket] 전송 완료 → 세션 ID: {}", session.getId());
                }
            }
        } catch (IOException e) {
            throw new WebSocketSendException("웹소켓 메시지 전송 실패 : {}", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractToken(session);
        WebSocketSession previous = sessions.put(token, session);

        if (previous != null && previous != session) {
            try {
                previous.close(CloseStatus.NORMAL);
                logger.info("[WebSocket] 중복 연결로 기존 세션 종료 : token={}", token);
            } catch (IOException e) {
                logger.error("[WebSocket] 기존 세션 종료 중 오류 발생: token={}", token);
            }
        }
        logger.info("[WebSocket] 연결 완료: token={}, session={}", token, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String token = extractToken(session);

        if (token != null && session.equals(sessions.get(token))) {
            sessions.remove(token);
            logger.info("[WebSocket] 연결 종료: token={}, status={}", token, status);
        }
    }

    private String extractToken(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return "anonymous";
        }

        String query = session.getUri().getQuery();
        for (String str : query.split("&")) {
            String[] split = str.split("=");
            if (split.length == 2 && split[0].equals("token")) {
                return split[1];
            }
        }
        return "anonymous";
    }
}
