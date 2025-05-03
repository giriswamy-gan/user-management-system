package com.apica.user_service.utils;

import org.springframework.http.HttpStatus;

public class CustomApiException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String customCode;
    private final boolean includeStackTrace;

    public CustomApiException(String message, HttpStatus statusCode, String customCode) {
        super(message);
        this.statusCode = statusCode;
        this.customCode = customCode;
        this.includeStackTrace = false;  // Default to false
    }

    public CustomApiException(String message, HttpStatus statusCode, String customCode, boolean includeStackTrace) {
        super(message);
        this.statusCode = statusCode;
        this.customCode = customCode;
        this.includeStackTrace = includeStackTrace;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getCustomCode() {
        return customCode;
    }

}