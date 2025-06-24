package org.example.qraft_technologies.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.exception.NewsNotFoundException;
import org.example.qraft_technologies.repository.TranslatedNewsRepository;
import org.example.qraft_technologies.websocket.WebSocketNewsSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsDeliveryService {

    private final NewsQueue newsQueue;
    private final TranslatedNewsRepository repository;
    private final WebSocketNewsSender sender;

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String newsId = newsQueue.take();
                    log.info("[NewsDelivery] 뉴스 ID 수신: {}", newsId);

                    TranslatedNews translatedNews = repository.findById(newsId)
                            .orElseThrow(() -> new NewsNotFoundException(newsId));
                    log.info("[NewsDelivery] 뉴스 DB 조회 성공: {}", translatedNews.getTitle());

                    sender.broadcast(translatedNews);
                    log.info("[NewDelivery] 뉴스 전송 완료: {}", newsId);

                } catch (NewsNotFoundException e) {
                    log.warn("[NewsDelivery] 뉴스 조회 실패: {}", e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[NewsDelivery] 스레드가 인터럽트되었습니다.");
                    break;
                } catch (Exception e) {
                    log.error("[NewsDelivery] 뉴스 처리 중 알 수 없는 오류 발생: {}", e.getMessage());
                }
            }
        }).start();
    }
}
