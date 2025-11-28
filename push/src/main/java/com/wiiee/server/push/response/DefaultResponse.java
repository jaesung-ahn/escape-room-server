package com.wiiee.server.push.response;

import lombok.Getter;

import java.util.List;

@Getter
public class DefaultResponse {

    private final String code;

    private final String message;

    private final List<String> errorDetails;

    public DefaultResponse(String code, String message, List<String> errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
    }
}
