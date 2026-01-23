package com.wiiee.server.api.application.user;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.user.Profile;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserGenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class UserProfileResponseDTO {

    @Schema(description = "유저 아이디")
    Long id;
    @Schema(description = "닉네임")
    String nickname;

    @Schema(description = "프로필 image url")
    String profileImgUrl;

    @Schema(description = "잼핏 테스트 유형")
    String zamfitTest;

    @Schema(description = "연령대")
    String ageGroup;

    @Schema(description = "성별")
    String userGender;

    @Schema(description = "대 지역")
    String state;
    @Schema(description = "소 지역")
    String city;

    public static UserProfileResponseDTO from(User user, Image userImage) {
        Profile profile = user.getProfile();
        String wbtiName = user.getProfile().getWbti() != null ? user.getProfile().getWbti().getName() : null;
        String ageGroup = profile.getBirthDate() != null ? LocalDateTimeUtil.getAgeGroup(profile.getBirthDate()) : null;
        City city = profile.getCity();
        String cityName = city != null ? city.getName() : null;
        String stateName = city != null ? State.valueOf(city.getParentCode()).getName() : null;
        String userGenderType = profile.getUserGenderType() != null ? profile.getUserGenderType().getName() : null;
        String profileImgUrl = userImage != null ? userImage.getUrl() : "";
        return new UserProfileResponseDTO(
                user.getId(), profile.getNickname(), profileImgUrl,
                wbtiName, ageGroup, userGenderType, stateName, cityName);
    }
}
