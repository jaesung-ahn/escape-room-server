package com.wiiee.server.api.application.user;

import com.wiiee.server.api.application.security.JwtModel;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserGenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//@Value
@Getter
@Setter
public class UserWithTokenModel {

    @Schema(description = "유저 아이디")
    Long id;
    @Schema(description = "이메일")
    String email;

    @Schema(description = "억세스 토큰")
    String accessToken;
    @Schema(description = "리프레시 토큰")
    String refreshToken;

    @Schema(description = "회원가입 여부")
    Boolean isSignUp;

    @Schema(description = "닉네임")
    String nickname;

    @Schema(description = "성별")
    String userGenderType;
    @Schema(description = "생일")
    LocalDate birthDate;
    @Schema(description = "관심 지역")
    String city;

    protected UserWithTokenModel() {
    }

    public UserWithTokenModel(Long id, String email, String accessToken, String refreshToken, Boolean isSignUp, String nickname, String userGenderType, LocalDate birthDate, String city) {
        this.id = id;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isSignUp = isSignUp;
        this.nickname = nickname;
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
        this.city = city;
    }

    public static UserWithTokenModel fromUserAndToken(User user, JwtModel jwtModel, Boolean isSignUp) {

        UserGenderType userGenderType = user.getProfile().getUserGenderType();
        City city = user.getProfile().getCity();

        return new UserWithTokenModel(user.getId(),
                user.getEmail(),
                jwtModel.getAccessToken(),
                jwtModel.getRefreshToken(),
                isSignUp,
                user.getProfile().getNickname(),
                userGenderType != null ? userGenderType.getName() : null,
                user.getProfile().getBirthDate(),
                city != null ? city.getName() : null
            );
    }

    public static UserWithTokenModel fromUserAndToken(User user, JwtModel jwtModel) {

        UserGenderType userGenderType = user.getProfile().getUserGenderType();
        City city = user.getProfile().getCity();

        return new UserWithTokenModel(user.getId(),
                user.getEmail(),
                jwtModel.getAccessToken(),
                jwtModel.getRefreshToken(),
                true,
                user.getProfile().getNickname(),
                userGenderType != null ? userGenderType.getName() : null,
                user.getProfile().getBirthDate(),
                city != null ? city.getName() : null
        );
    }
}
