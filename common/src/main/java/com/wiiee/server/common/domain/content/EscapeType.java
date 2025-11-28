package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum EscapeType implements EnumInterface {
    _0_10("0:10", 0),
    _1_9("1:9", 1),
    _2_8("2:8", 2),
    _3_7("3:7", 3),
    _4_6("4:6", 4),
    _5_5("5:5", 5),
    _6_4("6:4", 6),
    _7_3("7:3", 7),
    _8_2("8:2", 8),
    _9_1("9:1", 9),
    _10_0("10:0", 10);

    private final String name;
    private final Integer code;

    EscapeType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public static EscapeType valueOf(int code) {
        return Arrays.stream(EscapeType.values())
                .filter(escapeType -> escapeType.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported code %s.", code)));
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
