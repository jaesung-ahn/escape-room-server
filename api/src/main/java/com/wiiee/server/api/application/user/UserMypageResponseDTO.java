package com.wiiee.server.api.application.user;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.user.MemberType;
import com.wiiee.server.common.domain.user.Profile;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;


@Setter
@Getter
@Builder(access = PROTECTED)
public class UserMypageResponseDTO {

    @Schema(description = "유저 아이디")
    Long userId;

    @Schema(description = "유저 상태(정상: 0, 휴면: 1, 블락: 2, 탈퇴: 3)")
    Integer userStatusCode;

    @Schema(description = "이메일")
    String email;

    @Schema(description = "가입 플랫폼(유형) 종류")
    MemberType memberType;

    @Schema(description = "닉네임")
    String nickname;

    @Schema(description = "내 소개")
    String intro;

    @Schema(description = "프로필 이미지 url")
    String profileImgUrl;

    @Schema(description = "생일(yyyy-mm-dd)")
    LocalDate birthDate;
    @Schema(description = "성별")
    String userGender;

    @Schema(description = "연령대")
    String ageGroup;

    @Schema(description = "대 지역")
    String state;
    @Schema(description = "소 지역")
    String city;

    @Schema(description = "잼핏테스트")
    String zamfitTest;
    @Schema(description = "동행 수")
    int gatheringCnt;
    @Schema(description = "팔로워 수")
    int followerCnt = 0;
    @Schema(description = "팔로잉 수")
    int followingCnt = 0;

    @Schema(description = "state 코드값")
    Integer stateCode;
    @Schema(description = "city 코드값")
    Integer cityCode;

    UserPushNotiResponseDTO userPushNoti;

    @Builder
    public static UserMypageResponseDTO fromUserMypageResponseDTO(User user, String profileUrl, int gatheringCnt, String zamfitTest) {
        Profile profile = user.getProfile();

        City city = profile.getCity();
        String cityName = city != null ? city.getName() : null;
        String stateName = city != null ? State.valueOf(city.getParentCode()).getName() : null;

        Integer stateCode = city != null ? city.getParentCode() : null;
        Integer cityCode = city != null ? city.getCode() : null;

        String userGender = profile.getUserGenderType() != null ? profile.getUserGenderType().getName() : null;

        String ageGroup = profile.getBirthDate() != null ? LocalDateTimeUtil.getAgeGroup(profile.getBirthDate()) : null;


        return UserMypageResponseDTO.builder()
                .userId(user.getId())
                .userStatusCode(profile.getUserStatus().getCode())
                .email(user.getEmail())
                .memberType(profile.getMemberType())
                .nickname(profile.getNickname())
                .intro(profile.getIntro())
                .profileImgUrl(profileUrl)
                .birthDate(profile.getBirthDate())
                .userGender(userGender)
                .ageGroup(ageGroup)
                .state(stateName)
                .city(cityName)
                .zamfitTest(zamfitTest)
                .gatheringCnt(gatheringCnt)
                .followerCnt(0) // 미구현이라 0
                .followingCnt(0) // 미구현이라 0
                .stateCode(stateCode)
                .cityCode(cityCode)
                .userPushNoti(UserPushNotiResponseDTO.builder().
                        profile(profile).build())
                .build();
    }
}
