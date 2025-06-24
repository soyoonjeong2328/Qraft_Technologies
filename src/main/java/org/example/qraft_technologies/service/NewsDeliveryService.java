package org.example.qraft_technologies.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.exception.NewsNotFoundException;
import org.example.qraft_technologies.repository.TranslatedNewsRepository;
import org.example.qraft_technologies.websocket.WebSocketNewsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsDeliveryService {

    private final NewsQueue newsQueue;
    private final TranslatedNewsRepository repository;
    private final WebSocketNewsSender sender;
    private static final Logger logger = LoggerFactory.getLogger(NewsDeliveryService.class);

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String newsId = newsQueue.take();
                    logger.info("뉴스 ID 수신:{}", newsId);

                    TranslatedNews translatedNews = repository.findById(newsId)
                            .orElseThrow(() -> new NewsNotFoundException(newsId));
                    logger.info("뉴스 DB 조회 성공 : {}", translatedNews.getTitle());

                    sender.broadcast(translatedNews);
                    logger.info("뉴스 전송 완료: {}", newsId);
                } catch (NewsNotFoundException e) {
                    logger.warn("뉴스 조회 실패: {}", e.getMessage());
                } catch (Exception e) {
                    logger.error("뉴스 처리 중 알 수 없는 오류 발생 : {}", e.getMessage());
                }
            }
        }).start();
    }
}
