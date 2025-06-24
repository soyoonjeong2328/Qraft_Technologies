package org.example.qraft_technologies.exception;

public class WebSocketSendException extends RuntimeException {
    public WebSocketSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
