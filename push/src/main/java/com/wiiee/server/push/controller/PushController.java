package com.wiiee.server.push.controller;

import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.push.PushType;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserOS;
import com.wiiee.server.push.application.EventPushRequestDTO;
import com.wiiee.server.push.application.GatheringConfPushRequestDTO;
import com.wiiee.server.push.application.GatheringReqPushRequestDTO;
import com.wiiee.server.push.application.PushResponseDTO;
import com.wiiee.server.push.constant.PushMessageConst;
import com.wiiee.server.push.constant.ResponseConst;
import com.wiiee.server.push.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequestMapping("/api/push")
@RestController
@RequiredArgsConstructor
public class PushController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    private final EventService eventService;

    private final UserService userService;

    private final GatheringService gatheringService;

    private final PushHistoryService pushHistoryService;

    @Operation(summary = "이벤트 푸시 보내기", security = { })
    @PostMapping(value = "sendEventPush", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PushResponseDTO> sendEventPush(@Validated @RequestBody EventPushRequestDTO requestDTO) {

        log.debug(String.valueOf("sendEventPush requestDTO = " + requestDTO));
        // 해당 이벤트 존재하는지 체크
        Event event = eventService.findById(requestDTO.getEventId());
        log.debug(String.valueOf("event = " + event));
        System.out.println("event = " + event.getId());
        System.out.println("event = " + event.getTitle());

        List<User> allEventUsers = userService.findAllForEvent();
        int successCnt = 0;
        int failCnt = 0;
        Exception exception = null;
        for (User user : allEventUsers) {
            System.out.println("user = " + user.getProfile().getMemberType());
            System.out.println("user = " + user.getProfile().getNickname());
            System.out.println("user = " + user.getPushToken());
            try {
                if (user.getProfile().isPushEvent() && user.getProfile().getUserOs() != null && user.getProfile().getUserOs().equals(UserOS.AOS)) {
                    boolean isResult = firebaseCloudMessageService.sendPushMessage(user.getPushToken(),
                            requestDTO.getTitle(), requestDTO.getPushContent(),
                            PushType.EVENT_ALL_USER, requestDTO.getEventId());

                    if (isResult) {
                        successCnt += 1;
                    }
                    else {
                        failCnt += 1;
                    }

                    Thread.sleep(300);
                }

            } catch (IOException e) {
                failCnt += 1;
                exception = e;
            } catch (InterruptedException e) {
                failCnt += 1;
                exception = e;
            } catch (Exception e) {
                failCnt += 1;
                exception = e;
            }

        }
        pushHistoryService.updatePushHistory(requestDTO.getPushHistoryId(),
                successCnt, failCnt);

        if (failCnt > 0) {
            throw new RuntimeException(exception);
        }

        return ResponseEntity.ok().body(
                PushResponseDTO.fromPushResponseDTO(ResponseConst.SUCCESS_CODE, ResponseConst.SUCCESS_MSG)
        );
    }

    @Operation(summary = "동행 신청 푸시 보내기", security = { })
    @PostMapping(value = "sendGatheringRequestPush", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PushResponseDTO> sendGatheringRequestPush(@Validated @RequestBody GatheringReqPushRequestDTO requestDTO) {

        // 존재하는지 체크
        Gathering gathering = gatheringService.findById(requestDTO.getGatheringId());
        User leaderUser = userService.findById(requestDTO.getLeaderId());
        User requestUser = userService.findById(requestDTO.getUserId());
        HashMap<String, String> pushMessageMap = PushMessageConst.getPushMessageByType(
                PushType.GATHERING_REQUEST, gathering.getGatheringInfo().getTitle(), requestUser.getProfile().getNickname(),
                0);
        if (leaderUser.getProfile().isPushGathering() && leaderUser.getProfile().getUserOs() != null && leaderUser.getProfile().getUserOs().equals(UserOS.AOS)) {
            try {
                firebaseCloudMessageService.sendPushMessage(leaderUser.getPushToken(),
                        pushMessageMap.get(PushMessageConst.TITLE),
                        pushMessageMap.get(PushMessageConst.PUSH_CONTENT),
                        PushType.GATHERING_REQUEST, requestDTO.getGatheringMemberId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.ok().body(
                PushResponseDTO.fromPushResponseDTO(ResponseConst.SUCCESS_CODE, ResponseConst.SUCCESS_MSG)
        );
    }

    @Operation(summary = "동행신청에 수락 or 거절 푸시 보내기", security = { })
    @PostMapping(value = "sendGatheringConfirmPush", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PushResponseDTO> sendGatheringConfirmPush(@Validated @RequestBody GatheringConfPushRequestDTO requestDTO) {

        Gathering gathering = gatheringService.findById(requestDTO.getGatheringId());
        User user = userService.findById(requestDTO.getUserId());
        HashMap<String, String> pushMessageMap = PushMessageConst.getPushMessageByType(PushType.GATHERING_CONFIRM,
                gathering.getGatheringInfo().getTitle(), null,
                requestDTO.getConfirmCode());
        if (user.getProfile().isPushGathering() && user.getProfile().getUserOs() != null && user.getProfile().getUserOs().equals(UserOS.AOS)) {
            try {
                firebaseCloudMessageService.sendPushMessage(user.getPushToken(),
                        pushMessageMap.get(PushMessageConst.TITLE),
                        pushMessageMap.get(PushMessageConst.PUSH_CONTENT),
                        PushType.GATHERING_CONFIRM, requestDTO.getGatheringId());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.ok().body(
                PushResponseDTO.fromPushResponseDTO(ResponseConst.SUCCESS_CODE, ResponseConst.SUCCESS_MSG)
        );
    }
}
