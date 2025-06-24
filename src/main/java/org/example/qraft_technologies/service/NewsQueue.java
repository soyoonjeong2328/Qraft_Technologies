package org.example.qraft_technologies.service;

import org.example.qraft_technologies.entity.TranslatedNews;

public interface NewsQueue {
    void offer(TranslatedNews news);

    String poll();

    String take() throws InterruptedException;
}
