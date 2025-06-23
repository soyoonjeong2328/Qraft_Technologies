package org.example.qraft_technologies.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.repository.TranslatedNewsRepository;
import org.example.qraft_technologies.websocket.WebSocketNewsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsDeliveryService {

    private final NewsQueue newsQueue;
    private final TranslatedNewsRepository repository;
    private final WebSocketNewsSender sender;
    private static final Logger logger = LoggerFactory.getLogger(NewsDeliveryService.class);

    @PostConstruct
    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String newsId = newsQueue.take();
                    logger.info("뉴스 ID 수신:{}", newsId);
                    TranslatedNews translatedNews = repository.findById(newsId)
                            .orElseThrow(() -> new RuntimeException("뉴스 ID 찾을 수 없음 : " + newsId));
                    sender.broadcast(translatedNews);
                    logger.info("뉴스 DB 조회 성공 : {}", translatedNews.getTitle());
                } catch (Exception e) {
                    logger.error("[ERROR] 뉴스 전송 중 예외 발생 : {}", e.getMessage());
                }
            }
        }).start();
    }
}
