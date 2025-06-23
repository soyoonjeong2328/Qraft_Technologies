package org.example.qraft_technologies.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TRANSLATED_NEWS")
public class TranslatedNews {
    @Id
    private String id;
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name="published_at")
    private LocalDateTime publishedAt;
}
