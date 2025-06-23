package org.example.qraft_technologies.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class InMemoryQueue implements NewsQueue{
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public void offer(String newsId) {
        queue.offer(newsId);
    }

    @Override
    public String take() throws InterruptedException {
        return queue.take(); // 블로킹 방식
    }
}
