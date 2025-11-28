package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.UserOS;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserSnsRequestDTO {

    @Schema(description = "이메일")
    @NotNull(message = "이메일을 입력해주세요.")
    String email;

    @Schema(description = "sns 억세스 토큰")
    @NotNull(message = "억세스 토큰을 입력해주세요.")
    String accessToken;

    @Schema(description = "유저 OS")
    @NotNull(message = "유저 OS를 입력해주세요.")
    UserOS userOs;

    public UserSnSRequest toKakaoUserSnsRequest() {
        return UserSnSRequest.buildKakaoUser(email, accessToken, userOs);
    }

    @Builder
    public UserSnsRequestDTO(String email, String accessToken, UserOS userOs) {
        this.email = email;
        this.accessToken = accessToken;
        this.userOs = userOs;
    }
}
