package org.example.qraft_technologies.controller;

import lombok.RequiredArgsConstructor;
import org.example.qraft_technologies.service.NewsQueue;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Profile("dev")
public class TestController {

    private final NewsQueue queue;

    @PostMapping("/push/{id}")
    public ResponseEntity<Void> push(@PathVariable("id") String id) {
        queue.offer(id);
        return ResponseEntity.ok().build();
    }
}
