package com.wiiee.server.common.domain.company;

//import com.wiiee.server.common.domain.base.EnumInterface;
import com.wiiee.server.common.domain.EnumInterface;
import com.wiiee.server.common.domain.common.City;

import java.util.Arrays;


public enum CompanyBank implements EnumInterface {

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
    EXPORTIMPORTBANK("한국수출입은행", 12),
    IBKBANK("IBK기업은행", 13),
    KDBBANK("KDB산업은행", 14);

    private  String displayName;
    private  int code;

    CompanyBank() {
    }

    CompanyBank(String name, int code) {
        this.displayName = name;
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.displayName;
    }

    public static CompanyBank valueOf(int code) {
        return Arrays.stream(CompanyBank.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
