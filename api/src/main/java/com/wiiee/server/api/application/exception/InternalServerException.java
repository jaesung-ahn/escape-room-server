package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InternalServerException extends ApiException {

    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerException(ErrorCodeInterface errorCode) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }

    public InternalServerException(int code, String message, List<String> errorDetails) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, code, message, errorDetails);
    }
}
