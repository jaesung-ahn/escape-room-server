package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum UserOS implements EnumInterface {

    AOS("안드로이드", 0),
    IOS("아이폰", 1);

    private final String name;
    private final int code;

    UserOS(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static UserOS valueOf(int code) {
        return Arrays.stream(UserOS.values())
                .filter(userOS -> userOS.getCode() == code)
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
