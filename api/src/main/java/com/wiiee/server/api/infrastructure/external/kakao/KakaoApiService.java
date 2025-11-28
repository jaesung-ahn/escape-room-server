package com.wiiee.server.api.infrastructure.external.kakao;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@RequiredArgsConstructor
@Service
public class KakaoApiService {

    private final WebClient webClient;

    public KakaoUser getKakaoUser(String accessToken) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://kapi.kakao.com");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        URI uri = uriBuilderFactory.uriString("v2/user/me").build();

        ResponseEntity<String> response = webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class)
                .blockOptional().orElseThrow();

        JSONObject obj = new JSONObject(response.getBody());
        return KakaoUser.from(obj);
    }

}
