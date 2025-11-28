package com.wiiee.server.api.domain.code;

import com.wiiee.server.common.domain.ErrorCodeInterface;

public enum GatheringErrorCode implements ErrorCodeInterface {

    ERROR_GATHERING_REQUEST_IS_NOT_YOUR_REQ(8116, "동행모집 참가서가 본인 요청이 아닙니다."),
    ERROR_GATHERING_REQUEST_IS_NON_CANCELLABLE(8117, "취소할 수 없는 동행모집 참가서입니다."),
    ERROR_GATHERING_IS_NOT_MINE(8118, "본인 동행모집이 아니면 변경이 불가능합니다."),
    ERROR_DELETED_GATHERING(8119, "삭제된 동행모집입니다."),
    ERROR_NOT_ALLOWED_OPEN_CHAT_URL(8120, "허용되지 않은 오픈채팅 url입니다."),
    ERROR_NOT_ALLOWED_UNDER_THE_CURRENT_MEMBER(8121, "현재 수락된 멤버 수보다 적은 최대인원수로 변경할 수 없습니다."),
    ERROR_HOPE_DATE_NOT_ALLOWED_BEFORE_NOW(8122, "동행 희망일은 현재보다 빠른 날짜로 변경할 수 없습니다."),
    ;

    private final int code;
    private final String msg;

    GatheringErrorCode(int code, String msg) {
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
