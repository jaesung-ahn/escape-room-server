package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.EnumInterface;

public enum GatheringResultType implements EnumInterface {

    CHALLENGING_FOX("도전적인 여우", 0);

    private final String name;
    private final int code;

    GatheringResultType(String name, int code) {
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
