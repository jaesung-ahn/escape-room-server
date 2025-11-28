package com.wiiee.server.api.application.exception;

import com.wiiee.server.common.domain.ErrorCodeInterface;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomException extends RuntimeException {

    public final int code;
    public final String message;
    public final List<String> errorDetails;

    public CustomException(int code, String message, List<String> errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
    }

    public CustomException(ErrorCodeInterface errorCodeInf) {
        this.code = errorCodeInf.getCode();
        this.message = errorCodeInf.getMsg();
        this.errorDetails = null;
    }
}
