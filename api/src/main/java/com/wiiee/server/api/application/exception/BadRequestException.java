package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(ErrorCodeInterface errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }

    public BadRequestException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.BAD_REQUEST, code, message, errorDetails);
    }
}
