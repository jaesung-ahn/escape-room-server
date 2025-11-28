package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.user.UserGenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserSignupEtcRequestDTO {

    @Schema(description = "유저 아이디")
    @NotNull(message = "유저 아이디를 입력해주세요.")
    Long userId;

    @Schema(description = "닉네임")
    @NotNull(message = "닉네임을 입력해주세요.")
    @Size(max = 8, message = "닉네임은 8글자 이상은 허용하지 않습니다.")
    String nickname;

    @Schema(description = "성별")
    UserGenderType userGenderType;
    @Schema(description = "생일(yyyy-mm-dd)")
    LocalDate birthDate;
    @Schema(description = "지역 코드값")
    @NotNull(message = "지역 코드값을 입력해주세요.")
    Integer cityCode;

    @Builder
    public UserSignupEtcRequestDTO(Long userId, String nickname, UserGenderType userGenderType, LocalDate birthDate, Integer cityCode) {
        this.userId = userId;
        this.nickname = nickname;
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
        this.cityCode = cityCode;
    }


    public UserSignupEtcRequest toUpdateUserSignupEtc() {
        return new UserSignupEtcRequest(userId, nickname, userGenderType, birthDate, cityCode);
    }

    @Value
    public static class UserSignupEtcRequest {
        Long userId;
        String nickname;

        UserGenderType userGenderType;
        LocalDate birthDate;
        City city;

        public UserSignupEtcRequest(Long userId, String nickname, UserGenderType userGenderType, LocalDate birthDate, Integer cityCode) {
            this.userId = userId;
            this.nickname = nickname;
            this.userGenderType = userGenderType;
            this.birthDate = birthDate;
            this.city = cityCode != null ? City.valueOf(cityCode) : null;
        }
    }
}
