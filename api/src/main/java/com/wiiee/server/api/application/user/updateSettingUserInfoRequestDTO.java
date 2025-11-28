package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.UserGenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class updateSettingUserInfoRequestDTO {

    @Schema(description = "성별")
    @NotNull(message = "성별을 입력해주세요.")
    UserGenderType userGenderType;

    @Schema(description = "생일(yyyy-mm-dd)")
    @NotNull(message = "생일을 입력해주세요.")
    LocalDate birthDate;

    @Builder
    public updateSettingUserInfoRequestDTO(UserGenderType userGenderType, LocalDate birthDate) {
        this.userGenderType = userGenderType;
        this.birthDate = birthDate;
    }
}
