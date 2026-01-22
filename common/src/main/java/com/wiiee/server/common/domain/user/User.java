package com.wiiee.server.common.domain.user;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Embedded
    private Password password;

    @Embedded
    private Profile profile;

    private String refreshToken;
    // FCM 푸시 토큰
    @Column(length = 300)
    private String pushToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder(access = PROTECTED)
    private User(String email, Password password, Profile profile, String refreshToken, UserRole role) {
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.refreshToken = refreshToken;
        this.role = role != null ? role : UserRole.USER;
    }

    public User(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.profile = new Profile(nickname);
        this.role = UserRole.USER;
    }

    public User(String email, String nickname, Password password) {
        this.email = email;
        this.profile = new Profile(nickname);
        this.password = password;
        this.role = UserRole.USER;
    }

    public static User of(String email, String nickname) {
        return User.builder()
                .email(email)
                .profile(new Profile(nickname))
                .password(null)
                .refreshToken(null)
                .role(UserRole.USER)
                .build();
    }

    public static User snsSignupOf(String email, MemberType memberType, UserOS userOS) {
        return User.builder()
                .email(email)
                .profile(Profile.signUpSnsProfile(memberType, userOS))
                .password(null)
                .refreshToken(null)
                .role(UserRole.USER)
                .build();
    }

    public static User of(String email, String nickname, MemberType memberType, UserGenderType userGenderType,
                          Integer age, City city, LocalDate birthDate, UserOS userOs) {
        return User.builder()
                .email(email)
                .profile(new Profile(nickname, memberType, userGenderType, null, age, city, birthDate, userOs))
                .password(null)
                .refreshToken(null)
                .role(UserRole.USER)
                .build();
    }

    public static User ofWithRole(String email, String nickname, Password password, UserRole role) {
        return User.builder()
                .email(email)
                .profile(new Profile(nickname))
                .password(password)
                .refreshToken(null)
                .role(role)
                .build();
    }

    public Review addReviewToContent(Content content, String message, Double rating, Integer joinNumber, List<Long> imageIds,
                                     LocalDate realGatherDate) {
        return content.addReview(this, message, rating, joinNumber, imageIds, realGatherDate);
    }

    public Gathering addGathering(Content content, GatheringInfo gatheringInfo) {
        return new Gathering(content, this, gatheringInfo);
    }

    public void updateUser(Long userId, UserUpdateRequest request) {
        if(!id.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        profile.updateProfileIfPresent(request);
    }

    public void updatePushTokenAndUserOs(Long userId, String pushToken, UserOS userOSCode) {
        if(!id.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        this.pushToken = pushToken;
        if (userOSCode != null) {
            this.profile.changeUserOs(userOSCode);
        }
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void updateLastLoginDate(LocalDate lastLoginDate) {
        this.profile.updateLastLoginDate(lastLoginDate);
    }

    public void updateUserSignupEtc(String nickname, UserGenderType userGenderType, LocalDate birthDate, City city) {
        this.profile.updateUserSignupEtc(nickname, userGenderType, birthDate, city);
    }

    public void updateSettingUserInfo(UserGenderType userGenderType, LocalDate birthDate) {
        this.profile.updateSettingUserInfo(userGenderType, birthDate);
    }
}
