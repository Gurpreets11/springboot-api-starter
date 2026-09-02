package com.gurpreet.starter.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String entity, String field, Object value) {
        super(String.format("%s not found with %s : '%s'", entity, field, value), HttpStatus.NOT_FOUND);
    }
}
