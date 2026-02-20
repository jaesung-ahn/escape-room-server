package com.wiiee.server.api.domain.gathering;

import com.google.common.net.HttpHeaders;
import com.wiiee.server.api.config.properties.PushProperties;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatheringNotificationService {

    private final PushProperties pushProperties;

    /**
     * 동행 신청 푸시 요청
     */
    @Async
    public void sendGatheringRequestPush(Gathering gathering, GatheringRequest gatheringRequest) {
        log.info("call sendGatheringRequestPush()");

        if (!pushProperties.enabled()) {
            log.info("[MOCK] Push notification would be sent - GatheringId: {}, UserId: {}",
                    gathering.getId(), gatheringRequest.getRequestUser().getId());
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            HashMap<String, Object> requestMap = new HashMap<>();
            requestMap.put("gatheringId", gathering.getId());
            requestMap.put("gatheringMemberId", gatheringRequest.getId());
            requestMap.put("leaderId", gathering.getLeader().getId());
            requestMap.put("userId", gatheringRequest.getRequestUser().getId());

            String message = new JSONObject(requestMap).toJSONString();

            log.info("sendGatheringRequestPush request message = " + message);

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            String apiUrl = pushProperties.api().url() + "/sendGatheringRequestPush";

            Request requestObj = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(requestObj).execute();

            log.info("response.body():" + (response.body() != null ? response.body().string() : null));
        } catch (IOException e) {
            log.error("Failed to send push notification", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending push notification", e);
        }
    }

    /**
     * 동행 수락/거절 푸시 요청
     */
    @Async
    public void sendGatheringConfirmPush(Gathering gathering, GatheringRequest gatheringRequest,
                                          GatheringRequestStatus gatheringRequestStatus) {
        log.info("call sendGatheringConfirmPush()");

        if (!pushProperties.enabled()) {
            log.info("[MOCK] Push confirm notification would be sent - GatheringId: {}, UserId: {}, Status: {}",
                    gathering.getId(), gatheringRequest.getRequestUser().getId(), gatheringRequestStatus);
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            HashMap<String, Object> requestMap = new HashMap<>();
            requestMap.put("gatheringId", gathering.getId());
            requestMap.put("userId", gatheringRequest.getRequestUser().getId());
            requestMap.put("confirmCode", gatheringRequestStatus.getCode());

            String message = new JSONObject(requestMap).toJSONString();

            log.info("request message = " + message);

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            String apiUrl = pushProperties.api().url() + "/sendGatheringConfirmPush";

            Request requestObj = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(requestObj).execute();

            log.info("response.body():" + (response.body() != null ? response.body().string() : null));
        } catch (IOException e) {
            log.error("Failed to send push notification", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending push notification", e);
        }
    }
}
