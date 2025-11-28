package com.wiiee.server.common.domain.appVersion;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum AppOs implements EnumInterface {
    ANDROID("Android", 0),
    IOS("iOS", 1);

    private final String name;
    private final int code;

    AppOs(String name, int code) {
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

    public static AppOs valueOf(int code) {
        return Arrays.stream(AppOs.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }

}
