package com.wiiee.server.api.application.gathering.member;

import com.wiiee.server.api.application.user.UserSimpleModel;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class MemberModel {

    @Schema(description = "멤버 아이디")
    Long id;
    @Schema(description = "동행 모집 아이디")
    Long gatheringId;
    @Schema(description = "유저 아이디")
    Long userId;
    @Schema(description = "유저 프로필 url")
    String userProfileImageUrl;
    @Schema(description = "유저 닉네임")
    String userNickname;
    @Schema(description = "동행모집 리더 유무")
    Boolean isOwner;

    public static MemberModel fromMember(GatheringMember gatheringMember) {
        UserSimpleModel userSimpleModel = UserSimpleModel.fromUser(gatheringMember.getUser());
        return MemberModel.builder()
                .id(gatheringMember.getId())
                .gatheringId(gatheringMember.getGathering().getId())
                .userId(userSimpleModel.getId())
                .userProfileImageUrl(builder().userProfileImageUrl)
                .userNickname(userSimpleModel.getNickname())
                .isOwner(gatheringMember.getGathering().getLeader().getId().equals(gatheringMember.getUser().getId()))
                .build();
    }

}
