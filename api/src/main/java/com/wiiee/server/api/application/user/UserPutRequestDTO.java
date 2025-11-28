package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.user.UserUpdateRequest;
import lombok.Value;

@Value
public class UserPutRequestDTO {

    String nickname;
    String intro;
    Long profileImageId;
    Integer cityCode;
    Long wbtiId;

    public UserUpdateRequest toUpdateRequest() {
        return new UserUpdateRequest(nickname, intro, profileImageId, City.valueOf(cityCode), wbtiId);
    }

}
