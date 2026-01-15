package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
    private final List<String> errorDetails;

    protected ApiException(HttpStatus httpStatus, int code, String message, List<String> errorDetails) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
    }

    protected ApiException(HttpStatus httpStatus, ErrorCodeInterface errorCode) {
        super(errorCode.getMsg());
        this.httpStatus = httpStatus;
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
        this.errorDetails = null;
    }

    protected ApiException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = message;
        this.errorDetails = null;
    }
}
