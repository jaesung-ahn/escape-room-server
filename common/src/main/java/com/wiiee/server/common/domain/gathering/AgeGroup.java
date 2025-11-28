package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum AgeGroup implements EnumInterface {

//    IRRELEVANT("10대 미만", 0),
    TEENS("10대", 1),
    TWENTIES("20대", 2),
    THIRTIES("30대", 3),
    FORTIES("40대", 4),
    FIFTIES("50대 이상", 5);

    private final String name;
    private final int code;

    AgeGroup(String name, int code) {
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

    public static AgeGroup valueOf(int code) {
        return Arrays.stream(AgeGroup.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }

}
