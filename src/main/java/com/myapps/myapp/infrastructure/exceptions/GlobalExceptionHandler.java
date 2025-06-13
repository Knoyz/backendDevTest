package com.myapps.myapp.infrastructure.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.myapps.myapp.domain.exceptions.ConnectionException;
import com.myapps.myapp.domain.exceptions.InvalidResponseException;
import com.myapps.myapp.domain.exceptions.ServiceUnavailableException;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleWebClientNotFoundException(WebClientResponseException.NotFound e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Resource not found",
                e.getMessage(),
                e.getResponseBodyAsString(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(WebClientResponseException.ServiceUnavailable.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Error handleWebClientResponseException(WebClientResponseException.ServiceUnavailable e,
            ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("WebClient Error", e.getMessage(), e.getResponseBodyAsString(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @ExceptionHandler(InvalidResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleInvalidResponseException(InvalidResponseException e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Invalid Response", e.getMessage(), e.getClass().getName(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "BAD_REQUEST", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ConnectionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleConnectionException(ConnectionException e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Connection Error", e.getMessage(), e.getClass().getName(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Error handleServiceUnavailableException(ServiceUnavailableException e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Service Unavailable", e.getMessage(), e.getClass().getName(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleRuntimeException(RuntimeException e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Internal Server Error", e.getMessage(), e.getClass().getName(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleResourceNotFoundException(ResourceNotFoundException e, ServerWebExchange exchange) {
        String uriPath = exchange.getRequest().getURI().getPath();
        return new Error("Resource Not Found", e.getMessage(), e.getClass().getName(),
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                uriPath,
                "NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

}

@AllArgsConstructor
@Data
class Error {
    private String errorCode;
    private String message;
    private String details;
    private String timestamp;
    private String path;
    private String status;
    private int statusCode;

}
