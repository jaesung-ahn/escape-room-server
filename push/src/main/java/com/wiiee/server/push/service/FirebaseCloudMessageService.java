package com.wiiee.server.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.wiiee.server.common.domain.push.PushType;
import com.wiiee.server.push.message.FcmMessage;
import com.wiiee.server.push.util.PushMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    private final ObjectMapper objectMapper;

    public boolean sendPushMessage(String targetToken, String title, String pushContent, PushType pushType, Long id) throws IOException {
        log.debug("FirebaseCloudMessageService.sendMessageTo");
        String body = PushMessage.makePushBodyMessage(pushContent, pushType, id);
        log.debug(String.valueOf("body = " + body));
        String message = makeMessage(targetToken, title, body);

        log.debug(String.valueOf("message = " + message));

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        final String API_URL = "https://fcm.googleapis.com/v1/projects/dev-wiiee/messages:send";
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request)
                .execute();

        String responseBody = null;
        if (response.body() != null) {
            responseBody = response.body().string();
        }

        System.out.println("response.body():" + responseBody);

        if (responseBody != null && responseBody.contains("error")) {
            return false;
        }
        else {
            return true;
        }

    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {

        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(this.firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        String tokenValue = googleCredentials.getAccessToken().getTokenValue();
        log.debug(String.valueOf("tokenValue = " + tokenValue));
        return tokenValue;
    }
}
