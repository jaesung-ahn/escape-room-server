package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.EnumInterface;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ActivityLevel implements EnumInterface {
    EASY(0, "적음"),
    NORMAL(1, "보통"),
    HARD(2, "많음");

    private final int code;
    private final String name;

    ActivityLevel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ActivityLevel valueOf(int code) {
        return Arrays.stream(ActivityLevel.values())
                .filter(activityLevel -> activityLevel.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported code %s.", code)));
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
