package org.example.qraft_technologies.service;

import lombok.extern.slf4j.Slf4j;
import org.example.qraft_technologies.entity.TranslatedNews;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class InMemoryQueue implements NewsQueue {

    private static final int QUEUE_CAPACITY = 1000;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    @Override
    public void offer(TranslatedNews news) {
        boolean flag = queue.offer(news.getId());

        if (!flag) {
            log.warn("[Queue] 큐가 가득 찼습니다 → 누락된 뉴스 ID: {}", news.getId());
        } else {
            log.info("[Queue] 뉴스 ID 추가됨 → {}", news.getId());
        }
    }

    @Override
    public String poll() {
        String newsId = queue.poll();

        if (newsId != null) {
            log.info("[Queue] 뉴스 ID 꺼냄 → {}", newsId);
        } else {
            log.debug("[Queue] 큐가 비어 있음");
        }

        return newsId;
    }

    @Override
    public String take() throws InterruptedException {
        String newsId = queue.take();
        log.info("[Queue] 블로킹 큐에서 꺼냄 → {}", newsId);
        return newsId;
    }
}
