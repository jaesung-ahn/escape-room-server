package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.common.City;

import java.util.Optional;

import static java.util.Optional.*;

public class UserUpdateRequest {

    private final String nickname;
    private final String intro;
    private final Long profileImageId;
    private final Long wbtiId;
    private final City city;

    public UserUpdateRequest(String nickname, String intro, Long profileImageId, City city, Long wbtiId) {
        this.nickname = nickname;
        this.intro = intro;
        this.profileImageId = profileImageId;
        this.wbtiId = wbtiId;
        this.city = city;
    }

    public Optional<String> getNickname() {
        return ofNullable(nickname);
    }

    public Optional<String> getIntro() {
        return ofNullable(intro);
    }

    public Optional<Long> getProfileImageId() {
        return ofNullable(profileImageId);
    }

    public Optional<Long> getWbtiId() {
        return ofNullable(wbtiId);
    }

    public Optional<City> getCity() {
        return ofNullable(city);
    }

}
