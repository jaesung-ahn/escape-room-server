package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.MemberType;
import com.wiiee.server.common.domain.user.UserOS;
import lombok.AllArgsConstructor;
import lombok.Value;


@AllArgsConstructor
@Value
public class UserSnSRequest {

    String email;

    String accessToken;

    UserOS userOs;

    MemberType memberType;

    public static UserSnSRequest buildKakaoUser(String email, String accessToken, UserOS userOs) {
        return new UserSnSRequest(email, accessToken, userOs, MemberType.KAKAO);
    }
}
