package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.wiiee.server.common.domain.common.City.*;
import static com.wiiee.server.common.domain.user.UserStatus.*;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Profile {

    @Enumerated(value = EnumType.STRING)
    private MemberType memberType;

    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus = NORMAL;

    private String nickname;

    @Column(columnDefinition = "varchar(2000) default ''")
    private String intro;

    // 프로필 사진 id
    private Long profileImageId = 0L;

    @Enumerated(value = EnumType.STRING)
    private UserGenderType userGenderType;
    private Integer age;

    @Enumerated(value = EnumType.STRING)
    private City city;

    private String phoneNumber;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wbti_id",
            referencedColumnName = "wbti_id",
            nullable = true,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Wbti wbti;

    // 생년월일
    private LocalDate birthDate;

    // 로그인 날짜(휴면 회원 대비)
    private LocalDate lastLoginDate;

    // 이용 정지 숫자
    @ColumnDefault("0")
    private int blockCount = 0;

    // 놀거리 알림
    @Column(columnDefinition = "boolean default true")
    private boolean isPushContent = true;

    // 동행 알림
    @Column(columnDefinition = "boolean default true")
    private boolean isPushGathering = true;

    // 공지사항, 이벤트 알림
    @Column(columnDefinition = "boolean default true")
    private boolean isPushEvent = true;
    // 서비스 마케팅 수신 동의
    @Column(columnDefinition = "boolean default true")
    private boolean isAgreeServiceMarketing = true;

    // 서비스 마케팅 수신 동의 변경 일시
    private LocalDateTime updatedAgreeServiceMarketingDate;

    // 사용자 OS
    @Enumerated(value = EnumType.STRING)
    private UserOS userOs;

    @Builder(access = AccessLevel.PROTECTED)
    private Profile(String nickname, MemberType memberType, UserStatus userStatus, UserGenderType userGenderType, String phoneNumber, Integer age,
                    City city, Wbti wbti, LocalDate birthDate, LocalDate lastLoginDate, UserOS userOs) {
        this.nickname = nickname;
        this.memberType = memberType;
        this.userStatus = userStatus;
        this.userGenderType = userGenderType;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.city = city;
        this.wbti = wbti;
        this.birthDate = birthDate;
        this.lastLoginDate = lastLoginDate;
        this.userOs = userOs;
    }

    public static Profile signUpSnsProfile(MemberType memberType, UserOS userOS) {
        return new Profile(null, memberType, NORMAL, null, null, null, null, null, null, null, userOS);
    }

    public Profile(String nickname) {
        this(nickname, null, NORMAL, null, null, null, GANGNAMGU, null, null, null, null);
    }

    public Profile(String nickname, MemberType memberType, UserGenderType userGenderType, String phoneNumber,
                   Integer age, City city, LocalDate birthDate, UserOS userOs) {
        this(nickname, memberType, NORMAL, userGenderType, phoneNumber, age, city, null, birthDate,
                null, userOs);
    }

    public Profile(String nickname, Long profileImageId, City city, Wbti wbti) {
        this.nickname = nickname;
        this.profileImageId = profileImageId;
        this.city = city;
        this.wbti = wbti;
    }

    public void changeWbti(Wbti wbti) {
        this.wbti = wbti;
    }

    public void updateProfileIfPresent(UserUpdateRequest request) {
        request.getNickname().ifPresent(nicknameToUpdate -> nickname = nicknameToUpdate);
        request.getIntro().ifPresent(introToUpdate -> intro = introToUpdate);
        request.getProfileImageId().ifPresent(imageId -> profileImageId = imageId);
        request.getCity().ifPresent(cityToUpdate -> city = cityToUpdate);
    }

    public void updatePushStatusIfPresent(UserPushStatusDto userPushStatusDto){

        userPushStatusDto.getIsPushContent().ifPresent(pushContentValue -> isPushContent = pushContentValue);
        userPushStatusDto.getIsPushGathering().ifPresent(pushGatheringValue -> isPushGathering = pushGatheringValue);
        userPushStatusDto.getIsPushEvent().ifPresent(pushEventValue -> isPushEvent = pushEventValue);
        userPushStatusDto.getIsAgreeServiceMarketing().ifPresent(agreeServiceMarketing ->{
                isAgreeServiceMarketing = agreeServiceMarketing;
                updatedAgreeServiceMarketingDate = LocalDateTime.now();
        });
    }

    public void changeUserOs(UserOS userOs) {
        this.userOs = userOs;
    }



    public void updateLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void updateUserSignupEtc(String nickname, UserGenderType userGenderType, LocalDate birthDate, City city) {
        this.nickname = nickname;
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
        this.city = city;
    }

    public void updateSettingUserInfo(UserGenderType userGenderType, LocalDate birthDate) {
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
    }
}
