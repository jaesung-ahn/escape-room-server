package com.wiiee.server.common.domain.push;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum PushType implements EnumInterface {
    GATHERING_REQUEST("동행 신청", 0),
    GATHERING_CONFIRM("동행 수락 or 거절", 1),
    EVENT_ALL_USER("이벤트 전체 알림", 2);

    private final String name;
    private final int code;

    PushType(String name, int code) {
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

    public static PushType valueOf(int code) {
        return Arrays.stream(PushType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
