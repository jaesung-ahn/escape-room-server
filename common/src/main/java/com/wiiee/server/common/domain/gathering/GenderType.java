package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum GenderType implements EnumInterface {
    IRRELEVANT("성별무관", 0),
    ONLY_MAN("남자만", 1),
    ONLY_WOMAN("여자만", 2);

    private final String name;
    private final int code;

    GenderType(String name, int code) {
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

    public static GenderType valueOf(int code) {
        return Arrays.stream(GenderType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
