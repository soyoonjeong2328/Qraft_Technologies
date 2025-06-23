package org.example.qraft_technologies.dto;

import org.example.qraft_technologies.entity.TranslatedNews;

public record NewsDto(String id, String title, String body, String publishedAt) {
    public static NewsDto from(TranslatedNews news) {
        return new NewsDto(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getPublishedAt().toString()
        );
    }
}