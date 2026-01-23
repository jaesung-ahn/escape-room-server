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
    ERROR_GATHERING_REQUEST_NOT_ACCESSIBLE(8123, "참가서 조회 권한이 없습니다. (호스트 또는 신청자만 가능)"),
    ERROR_GATHERING_NOT_HOST(8124, "호스트만 수행할 수 있는 작업입니다."),
    ERROR_GATHERING_ALREADY_FAVORITED(8125, "이미 찜한 동행모집입니다."),
    ERROR_MEMBER_REGISTRATION_NOT_ALLOWED(8126, "멤버 등록이 불가능한 유저입니다."),
    ERROR_RECRUIT_COMPLETED(8110, "이미 완료된 동행모집 입니다."),
    ERROR_RECRUIT_EXPIRED(8111, "만료된 동행모집 입니다."),
    ERROR_RECRUIT_MAX_MEMBER(8112, "동행 메이트가 마감된 동행모집 입니다."),
    ERROR_RECRUIT_ALREADY_APPLIED(8113, "이미 신청된 동행모집 입니다."),
    ERROR_GATHERING_REQUEST_INVALID_STATUS(8114, "처리할 수 없는 상태의 동행모집 신청서 입니다."),
    ERROR_GATHERING_MEMBER_ALREADY_EXISTS(8115, "이미 존재하는 동행모집의 크루 입니다."),
    ERROR_GATHERING_MEMBER_NOT_FOUND(8127, "동행모집의 멤버가 아닙니다."),
    ERROR_APPROVED_REQUEST_NOT_FOUND(8128, "승인된 동행모집 신청서를 찾을 수 없습니다."),
    ERROR_MEMBER_UPDATE_PERMISSION_DENIED(8129, "본인의 멤버 정보만 수정할 수 있습니다."),
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
