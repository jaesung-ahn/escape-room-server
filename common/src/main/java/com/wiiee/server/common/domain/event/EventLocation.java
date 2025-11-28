package com.wiiee.server.common.domain.event;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum EventLocation implements EnumInterface {

    MAIN_BANNER("메인 배너", 0),
    ROTATION_BANNER1("상단 캐러셀 배너", 1),
    ROTATION_BANNER2("하단 캐러셀 배너", 2);

    private final String name;
    private final int code;

    EventLocation(String name, int code) {
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

    public static EventLocation valueOf(int code) {
        return Arrays.stream(EventLocation.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
