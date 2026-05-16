package com.titan.dispatch.domain.exception;

public class SafetyInterlockException extends RuntimeException {

    public SafetyInterlockException(String message) {
        super(message);
    }

    public SafetyInterlockException(String message, Throwable cause) {
        super(message, cause);
    }
}