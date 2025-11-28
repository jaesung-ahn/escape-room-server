package com.wiiee.server.common.domain.company;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum Bank implements EnumInterface {

    NHBANK("NH농협은행", 1),
    SHBANK("Sh수협은행", 2),
    KBBANK("KB국민은행", 3),
    WOORIBANK("우리은행", 4),
    SHINHANBANK("신한은행", 5),
    HANABANK("하나은행", 6),
    SCBANK("SC제일은행", 7),
    CITYBANK("한국씨티은행", 8),
    KBANK("케이뱅크", 9),
    KAKAOBANK("카카오뱅크", 10),
    TOSSBANK("토스뱅크", 11),
    EXPORTIMPORT("한국수출입은행", 12),
    IBKBANK("IBK기업은행", 13),
    KDBBANK("KDB산업은행", 14);

    private final String name;
    private final int code;

    Bank(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static Bank valueOf(int code) {
        return Arrays.stream(Bank.values())
                .filter(bank -> bank.getCode() == code)
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
