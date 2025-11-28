package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.EnumInterface;

public enum UserStatus implements EnumInterface {

    NORMAL("정상", 0),
    DORMANT("휴면", 1),
    BLOCK("블락", 2),
    WITHDRAWAL("탈퇴", 3);

    private final String name;
    private final int code;

    UserStatus(String name, int code) {
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
}
