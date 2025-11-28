package com.wiiee.server.api.application.user;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.gathering.GenderType;
import com.wiiee.server.common.domain.user.Profile;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class UserModel {

    @Schema(description = "유저 아이디")
    Long id;
    @Schema(description = "이메일")
    String email;
    @Schema(description = "닉네임")
    String nickname;
    @Schema(description = "프로필 사진")
    String image;
    @Schema(description = "대 지역")
    String state;
    @Schema(description = "소 지역")
    String city;
    @Schema(description = "성별")
    String userGender;
    @Schema(description = "연령대")
    String ageGroup;
    @Schema(description = "잼핏테스트")
    String zamfitTest;

    public static UserModel from(User user) {
        Profile profile = user.getProfile();

        City city = profile.getCity();
        String cityName = city != null ? city.getName() : null;
        String stateName = city != null ? State.valueOf(city.getParentCode()).getName() : null;
        String ageGroup = profile.getBirthDate() != null ? LocalDateTimeUtil.getAgeGroup(profile.getBirthDate()) : null;

        return UserModel.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(profile.getNickname())
//                .image(profileUrl)
                .state(stateName)
                .city(cityName)
                .userGender(profile.getUserGenderType().getName())
                .ageGroup(ageGroup)
                .zamfitTest(profile.getWbti().getName())
                .build();
    }

}
