package com.github.rafaellbarros.worker.exception;

public class PaymentResponseException extends RuntimeException {
    public PaymentResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}