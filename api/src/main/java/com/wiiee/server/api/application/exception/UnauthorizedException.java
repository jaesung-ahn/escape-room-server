package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException(ErrorCodeInterface errorCode) {
        super(HttpStatus.UNAUTHORIZED, errorCode);
    }

    public UnauthorizedException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.UNAUTHORIZED, code, message, errorDetails);
    }
}
