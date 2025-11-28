package com.wiiee.server.common.domain.gathering.member;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum Status implements EnumInterface {
    WAITING(0, "대기"),
    APPROVAL(1, "승인"),
    REJECT(2, "거절");

    private int code;
    private String name;

    Status(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Status valueOf(int code) {
        return Arrays.stream(Status.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
