package com.github.rafaellbarros.worker.exception;

public class ResponseSendingException extends RuntimeException {
    public ResponseSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
