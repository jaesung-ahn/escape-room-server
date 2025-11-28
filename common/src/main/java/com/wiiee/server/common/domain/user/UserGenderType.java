package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum UserGenderType implements EnumInterface {
    NONE("없음", 0),
    MALE("남자", 1),
    FEMALE("여자", 2);

    private final String name;
    private final int code;

    UserGenderType(String name, int code) {
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

    public static UserGenderType valueOf(int code) {
        return Arrays.stream(UserGenderType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
