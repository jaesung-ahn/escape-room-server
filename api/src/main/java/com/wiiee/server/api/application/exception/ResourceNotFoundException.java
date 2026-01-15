package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(ErrorCodeInterface errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }

    public ResourceNotFoundException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.NOT_FOUND, code, message, errorDetails);
    }
}
