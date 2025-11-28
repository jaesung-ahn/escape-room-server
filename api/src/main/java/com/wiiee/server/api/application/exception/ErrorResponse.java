package com.wiiee.server.api.application.exception;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;
    private final List<String> errorDetails;
    private final String responseTime;

    public ErrorResponse(int code, String message, List<String> errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowString("yyyy-MM-dd hh:mm:ss");
    }
}
