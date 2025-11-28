package com.wiiee.server.api.domain.code;

import org.springframework.http.HttpStatus;

public class StatusCode {

    public static final int OK_CODE = HttpStatus.OK.value();

    public static final int ERROR_WITHDRAWAL_USER_CODE = 8100;
    public static final String ERROR_WITHDRAWAL_USER_MSG = "탈퇴한 유저입니다.";

    public static final int ERROR_NO_EXIST_ZAMFIT_TEXT_CODE = 8120;
    public static final String ERROR_NO_EXIST_ZAMFIT_TEXT_MSG = "존재하지 않는 잼핏테스트 입니다.";

    public static final int ERROR_RECRUIT_COMPLETED_CODE = 8110;
    public static final String ERROR_RECRUIT_COMPLETED_MSG = "이미 완료된 동행모집 입니다.";

    public static final int ERROR_RECRUIT_EXPIRED_CODE = 8111;
    public static final String ERROR_RECRUIT_EXPIRED_MSG = "만료된 동행모집 입니다.";

    public static final int ERROR_RECRUIT_MAX_MB_CODE = 8112;
    public static final String ERROR_RECRUIT_MAX_MB_MSG = "동행 메이트가 마감된 동행모집 입니다.";

    public static final int ERROR_RECRUIT_SAME_REQUSER_CODE = 8113;
    public static final String ERROR_RECRUIT_SAME_REQUSER_MSG = "이미 신청된 동행모집 입니다.";

    public static final int ERROR_GATHERING_REQUEST_STATUS_CODE = 8114;
    public static final String ERROR_GATHERING_REQUEST_STATUS_MSG = "처리할 수 없는 상태의 동행모집 신청서 입니다.";

    public static final int ERROR_GATHERING_ALREADY_EXIST_MEMBER_CODE = 8115;
    public static final String ERROR_GATHERING_ALREADY_EXIST_MEMBER_MSG = "이미 존재하는 동행모집의 크루 입니다.";

    public static final int ERROR_REQUEST_CODE = HttpStatus.BAD_REQUEST.value();
    public static final String ERROR_REQUEST_MSG = HttpStatus.BAD_REQUEST.getReasonPhrase();

}
