package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum UserErrorCode implements ErrorCodeInterface {

    ERROR_EMAIL_ALREADY_EXISTS(1001, "이미 존재하는 이메일입니다."),
    ERROR_USER_BLOCKED(1002, "차단된 유저입니다."),
    ERROR_USER_DORMANT(1003, "현재 휴면 상태입니다. 고객센터에 문의해 휴면 해제바랍니다."),
    ERROR_USER_WITHDRAWN(8100, "탈퇴한 유저입니다."),
    ;

    private final int code;
    private final String msg;

    UserErrorCode(int code, String msg) {
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
