package com.wiiee.server.api.infrastructure.external.kakao;

import lombok.Builder;
import lombok.Value;
import org.json.JSONObject;

import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class KakaoUser {

    String email;
    String nickname;
    String imageUrl;

    private KakaoUser(String email, String nickname, String imageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public static KakaoUser from(JSONObject obj) {
        return new KakaoUser(Optional.ofNullable(obj.getJSONObject("kakao_account").getString("email")).orElse(""),
                Optional.ofNullable(obj.getJSONObject("properties").getString("nickname")).orElse(""),
                Optional.ofNullable(obj.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url")).orElse(""));
    }

}
