package org.example.qraft_technologies.exception;

public class NewsNotFoundException extends RuntimeException {
    public NewsNotFoundException(String newsId) {
        super("뉴스 ID [" + newsId + "]를 찾을 수 없습니다.");
    }
}
