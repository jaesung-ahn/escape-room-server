package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum Difficulty implements EnumInterface {
    LEVEL1("1", 1),
    LEVEL2("2", 2),
    LEVEL3("3", 3),
    LEVEL4("4", 4),
    LEVEL5("5", 5);

    private final String name;
    private final int code;

    Difficulty(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static Difficulty valueOf(int code) {
        return Arrays.stream(Difficulty.values())
                .filter(difficulty -> difficulty.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported code %s.", code)));
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
