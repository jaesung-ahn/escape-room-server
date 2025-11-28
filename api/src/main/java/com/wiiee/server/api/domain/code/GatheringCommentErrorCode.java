package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum GatheringCommentErrorCode implements ErrorCodeInterface {

    ERROR_PERMISSION_NOT_ALLOWED(7001, "댓글의 작성자가 아니면 할 수 없는 권한입니다."),
    ;

    private final int code;
    private final String msg;

    GatheringCommentErrorCode(int code, String msg) {
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
