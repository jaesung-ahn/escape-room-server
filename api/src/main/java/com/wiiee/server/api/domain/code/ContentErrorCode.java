package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum ContentErrorCode implements ErrorCodeInterface {

    ERROR_CONTENT_ALREADY_FAVORITED(2001, "이미 찜한 컨텐츠입니다."),
    ;

    private final int code;
    private final String msg;

    ContentErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
