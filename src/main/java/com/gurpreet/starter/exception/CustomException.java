package com.gurpreet.starter.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all business/application exceptions.
 * Carries an HTTP status so GlobalExceptionHandler can map it directly.
 */
public class CustomException extends RuntimeException {

    private final HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
