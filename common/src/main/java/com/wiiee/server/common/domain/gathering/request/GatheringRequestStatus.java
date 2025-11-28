package com.wiiee.server.common.domain.gathering.request;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum GatheringRequestStatus implements EnumInterface {

    UNVERIFIED(0, "호스트 확인 전"),
    VERIFIED(1, "호스트 확인 됨"),
    APPROVAL(2, "승인"),
    REJECT(3, "거절"),
    CANCELED(4, "요청자 취소(승인 전)"),
    CANCELED_JOIN(5, "요청자 참여 취소(승인 후)");

    private int code;
    private String name;

    GatheringRequestStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static GatheringRequestStatus valueOf(int code) {
        return Arrays.stream(GatheringRequestStatus.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", code)));
    }
}
