package org.example.qraft_technologies.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.dto.NewsDto;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class WebSocketNewsSender {

    private final NewsWebSocketHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void broadcast(TranslatedNews translatedNews) {
        try {
            String json = objectMapper.writeValueAsString(NewsDto.from(translatedNews));
            for(WebSocketSession session : handler.getSessions()) {
                if(session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            System.out.println("[웹소켓 전송 실패] : " + e.getMessage());
        }
    }
}
