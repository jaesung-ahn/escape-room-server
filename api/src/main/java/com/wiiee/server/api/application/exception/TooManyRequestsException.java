package com.wiiee.server.api.application.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class TooManyRequestsException extends ApiException {

    public TooManyRequestsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }

    public TooManyRequestsException(String message, List<String> errorDetails) {
        super(HttpStatus.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS.value(), message, errorDetails);
    }
}
