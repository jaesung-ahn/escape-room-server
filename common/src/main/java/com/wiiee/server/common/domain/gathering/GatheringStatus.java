package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.EnumInterface;

public enum GatheringStatus implements EnumInterface {

    RECRUITING("모집중", 0),
    RECRUIT_COMPLETED("모집 완료", 1),
    RECRUIT_CANCELED("모집 취소", 2),
    RECRUIT_EXPIRED("모집 만료", 3),
    DEADLINE_IMMINENT("마감 임박", 4);

    private final String name;
    private final int code;

    GatheringStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }
}
