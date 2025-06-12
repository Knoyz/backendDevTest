package com.myapps.myapp.domain.exceptions;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}