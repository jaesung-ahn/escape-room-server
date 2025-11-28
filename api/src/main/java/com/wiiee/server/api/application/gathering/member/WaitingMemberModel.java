package com.wiiee.server.api.application.gathering.member;

import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class WaitingMemberModel {

    @Schema(description = "요청서 아이디")
    Long gatheringReqeustId;

    @Schema(description = "유저 아이디")
    Long userId;

    @Schema(description = "유저 프로필 url")
    String userProfileImageUrl;
    @Schema(description = "유저 닉네임")
    String userNickname;

    public static WaitingMemberModel fromGatheringRequest(GatheringRequest gatheringRequest, String profileImageUrl) {
        return WaitingMemberModel.builder()
                .gatheringReqeustId(gatheringRequest.getId())
                .userId(gatheringRequest.getRequestUser().getId())
                .userProfileImageUrl(profileImageUrl)
                .userNickname(gatheringRequest.getRequestUser().getProfile().getNickname())
                .build();
    }
}
