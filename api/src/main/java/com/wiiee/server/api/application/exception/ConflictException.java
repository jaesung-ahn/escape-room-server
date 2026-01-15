package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public ConflictException(ErrorCodeInterface errorCode) {
        super(HttpStatus.CONFLICT, errorCode);
    }

    public ConflictException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.CONFLICT, code, message, errorDetails);
    }
}
