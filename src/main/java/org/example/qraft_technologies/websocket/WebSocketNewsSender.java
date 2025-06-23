package org.example.qraft_technologies.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.dto.NewsDto;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class WebSocketNewsSender {

    private final NewsWebSocketHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketNewsSender.class);

    public void broadcast(TranslatedNews translatedNews) {
        try {
            String json = objectMapper.writeValueAsString(NewsDto.from(translatedNews));
            logger.info("웹소켓으로 뉴스 전송 시작 : {}", translatedNews.getId());

            for (WebSocketSession session : handler.getSessions()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                    logger.info("전송 완료 → 세션 ID: {}", session.getId());
                }
            }
        } catch (Exception e) {
            logger.error("[웹소켓 전송 실패] - 뉴스 ID: {}", translatedNews.getId(), e);
        }
    }
}
