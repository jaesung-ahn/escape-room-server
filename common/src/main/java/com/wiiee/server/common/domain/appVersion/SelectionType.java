package com.wiiee.server.common.domain.appVersion;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum SelectionType implements EnumInterface {
    REQUIRE("필수", 0),
    OPTION("선택", 1),
    NOTHING("업데이트 없음", 2);

    private final String name;
    private final int code;

    SelectionType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static SelectionType valueOf(int code) {
        return Arrays.stream(SelectionType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
