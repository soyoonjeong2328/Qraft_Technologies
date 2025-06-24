package org.example.qraft_technologies.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qraft_technologies.repository.TranslatedNewsRepository;
import org.example.qraft_technologies.service.NewsQueue;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Profile("dev")
@Slf4j
public class TestController {
    private final TranslatedNewsRepository repository;
    private final NewsQueue newsQueue;

    @PostMapping("/push/{id}")
    public boolean push(@PathVariable String id) {
        return repository.findById(id)
                .map(news -> {
                    newsQueue.offer(news);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("[Push 요청 실패] 존재하지 않는 뉴스 ID: {}", id);
                    return false;
                });
    }
}
