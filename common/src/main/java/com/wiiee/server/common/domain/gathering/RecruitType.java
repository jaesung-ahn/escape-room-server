package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum RecruitType implements EnumInterface {
    ALL("전체", 2),
    CONFIRM("승낙제", 0),
    FIRST_COME("선착순", 1);

    private final String name;
    private final int code;

    RecruitType(String name, int code) {
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

    public static RecruitType valueOf(int code) {
        return Arrays.stream(RecruitType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}