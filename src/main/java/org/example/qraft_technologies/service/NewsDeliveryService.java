package org.example.qraft_technologies.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.example.qraft_technologies.repository.TranslatedNewsRepository;
import org.example.qraft_technologies.websocket.WebSocketNewsSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsDeliveryService {

    private final NewsQueue newsQueue;
    private final TranslatedNewsRepository repository;
    private final WebSocketNewsSender sender;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String newsId = newsQueue.take();
                    TranslatedNews translatedNews = repository.findById(newsId)
                            .orElseThrow(() -> new RuntimeException("뉴스 ID 찾을 수 없음 : " + newsId));
                    sender.broadcast(translatedNews);
                }catch (Exception e) {
                    System.out.println("[Error] 뉴스 전송 중 예외 발생 : " + e.getMessage());
                }
            }
        }).start();
    }
}
