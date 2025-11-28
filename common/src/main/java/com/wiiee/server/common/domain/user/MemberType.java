package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.EnumInterface;

public enum MemberType implements EnumInterface {
    KAKAO("카카오", 0),
    EMAIL("이메일", 1),
    NAVER("네이버", 2),
    FACEBOOK("페이스북", 3),
    GOOGLE("구글", 4);

    private final String name;
    private final int code;

    MemberType(String name, int code) {
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
