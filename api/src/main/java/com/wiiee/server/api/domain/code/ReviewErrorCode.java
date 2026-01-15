package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum ReviewErrorCode implements ErrorCodeInterface {

    ERROR_REVIEW_UPDATE_PERMISSION_DENIED(3001, "해당 리뷰 수정 권한이 없습니다."),
    ERROR_REVIEW_DELETE_PERMISSION_DENIED(3002, "해당 리뷰 삭제 권한이 없습니다."),
    ;

    private final int code;
    private final String msg;

    ReviewErrorCode(int code, String msg) {
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
