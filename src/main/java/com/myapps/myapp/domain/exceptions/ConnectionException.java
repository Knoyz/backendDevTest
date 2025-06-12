package com.myapps.myapp.domain.exceptions;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String message) {
        super(message);
    }
}