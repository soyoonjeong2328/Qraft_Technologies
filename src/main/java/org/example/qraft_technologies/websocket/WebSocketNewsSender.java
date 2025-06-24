package org.example.qraft_technologies.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qraft_technologies.dto.NewsDto;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.exception.WebSocketSendException;
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
@Slf4j
public class WebSocketNewsSender extends TextWebSocketHandler {

    private final NewsWebSocketHandler handler;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void broadcast(TranslatedNews translatedNews) {
        try {
            String json = objectMapper.writeValueAsString(NewsDto.from(translatedNews));
            log.info("[WebSocket] 뉴스 전송 시작 : {}", translatedNews.getId());

            for (WebSocketSession session : handler.getSessions()) {
                if (!session.isOpen()) {
                    log.warn("닫힌 세션 → 전송 생략: 세션 ID={}", session.getId());
                    continue;
                }

                try {
                    session.sendMessage(new TextMessage(json));
                    log.info("전송 완료 → 세션 ID={}", session.getId());
                } catch (IOException e) {
                    log.error("세션 전송 중 오류 발생 → 세션 ID={}, 뉴스 ID={}", session.getId(), translatedNews.getId(), e);
                }
            }

        } catch (IOException e) {
            throw new WebSocketSendException("웹소켓 메시지 전송 실패 : {}" + e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = (String) session.getAttributes().get("token");

        WebSocketSession previous = sessions.put(token, session);

        if (previous != null && previous != session) {
            try {
                previous.close(CloseStatus.NORMAL);
                log.info("[WebSocket] 중복 연결로 기존 세션 종료: token={}", token);
            } catch (IOException e) {
                log.warn("[WebSocket] 기존 세션 종료 중 오류 발생: token={}", token, e);
            }
        }

        log.info("[WebSocket] 새로운 연결 완료: token={}, sessionId={}", token, session.getId());
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String token = (String) session.getAttributes().get("token");

        if (token != null && session.equals(sessions.get(token))) {
            sessions.remove(token);
            log.info("[WebSocket] 연결 종료: token={}, sessionId={}, status={}", token, session.getId(), status);
        }
    }

}
