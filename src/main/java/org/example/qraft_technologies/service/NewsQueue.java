package org.example.qraft_technologies.service;

public interface NewsQueue {
    void offer(String newsId);
    String take() throws InterruptedException;
}
