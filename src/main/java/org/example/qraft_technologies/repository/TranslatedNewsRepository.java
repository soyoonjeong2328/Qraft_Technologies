package org.example.qraft_technologies.repository;

import org.example.qraft_technologies.entity.TranslatedNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslatedNewsRepository extends JpaRepository<TranslatedNews, String> {
}
