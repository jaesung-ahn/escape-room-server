package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public ForbiddenException(ErrorCodeInterface errorCode) {
        super(HttpStatus.FORBIDDEN, errorCode);
    }

    public ForbiddenException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.FORBIDDEN, code, message, errorDetails);
    }
}
