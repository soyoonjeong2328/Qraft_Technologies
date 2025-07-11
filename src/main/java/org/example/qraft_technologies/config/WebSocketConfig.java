package org.example.qraft_technologies.config;

import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.websocket.NewsWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NewsWebSocketHandler newsWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(newsWebSocketHandler, "/ws/news")
                .setAllowedOrigins("*");
    }
}
