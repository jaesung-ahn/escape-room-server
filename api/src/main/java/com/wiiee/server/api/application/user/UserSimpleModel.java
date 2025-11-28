package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class UserSimpleModel {

    @Schema(description = "유저 아이디")
    Long id;
    @Schema(description = "닉네임")
    String nickname;
//    String imageUrl;

    public static UserSimpleModel fromUser(User user) {
        return UserSimpleModel.builder()
                .id(user.getId())
                .nickname(user.getProfile().getNickname())
//                .imageUrl(user.get)
                .build();
    }

}
