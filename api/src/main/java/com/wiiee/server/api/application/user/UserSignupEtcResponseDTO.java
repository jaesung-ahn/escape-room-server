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
public class UserSignupEtcResponseDTO {

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
    @Schema(description = "지역값")
    City city;

    @Builder
    public UserSignupEtcResponseDTO(Long userId, String nickname, UserGenderType userGenderType, LocalDate birthDate, City city) {
        this.userId = userId;
        this.nickname = nickname;
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
        this.city = city;
    }

}
