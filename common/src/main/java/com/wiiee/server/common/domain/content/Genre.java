package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.EnumInterface;

import java.util.Arrays;

public enum Genre implements EnumInterface {

    THRILLER("스릴러", 0),
    HORROR("호러/공포", 1),
    ADVENTURE("모험/탐험", 2),
    EMOTION("감성", 3),
    REASONING("추리", 4),
    MYSTERY("미스터리", 5),
    FANTASY("판타지", 6),
    HISTORY("역사", 7),
    INFILTRATION("잠입", 8),
    DRAMA("드라마", 9),
    COMEDY("코미디", 10),
    SF("SF", 11),
    ETC("기타", 12);

    private final String name;
    private final int code;

    Genre(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static Genre valueOf(int code) {
        return Arrays.stream(Genre.values())
                .filter(genre -> genre.getCode() == code)
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
