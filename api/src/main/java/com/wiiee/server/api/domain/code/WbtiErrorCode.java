package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum WbtiErrorCode implements ErrorCodeInterface {

    ERROR_WBTI_NOT_FOUND(8120, "존재하지 않는 잼핏테스트 입니다."),
    ;

    private final int code;
    private final String msg;

    WbtiErrorCode(int code, String msg) {
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
