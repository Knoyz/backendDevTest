package com.myapps.myapp.domain.exceptions;

// Excepciones personalizadas
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}