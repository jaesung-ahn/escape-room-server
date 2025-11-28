package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.UserOS;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class UserPushInfoRequestDTO {

    @NotNull(message = "코드를 입력하세요.")
    UserOS userOSCode;
    String pushToken;

    @Builder
    public UserPushInfoRequestDTO(UserOS userOSCode, String pushToken) {
        this.userOSCode = userOSCode;
        this.pushToken = pushToken;
    }
}
