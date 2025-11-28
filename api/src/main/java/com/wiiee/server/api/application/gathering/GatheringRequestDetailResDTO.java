package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;


@Builder(access = PROTECTED)
@Value
@ToString
public class GatheringRequestDetailResDTO {

    @Schema(description = "동행모집 신청서 아이디")
    Long gatheringRequestId;

    @Schema(description = "동행모집 타이틀")
    String title;

    @Schema(description = "신청자 닉네임")
    String reqNickName;

    @Schema(description = "신청자 프로필 이미지 url")
    String reqProfileImgUrl;

    @Schema(description = "신청자 잼핏테스트명")
    String reqZamfitTest;

    @Schema(description = "신청자 연령그룹, 성별")
    String reqAgeGroupGender;

    @Schema(description = "신청자 지역명")
    String reqStateCity;

    @Schema(description = "호스트 이름")
    String hostName;

    @Schema(description = "신청 내용")
    String reqReason;

    @Schema(description = "동행모집 신청 상태")
    GatheringRequestStatus gatheringRequestStatus;

    public static GatheringRequestDetailResDTO fromGatheringRequestDetail(GatheringRequest gatheringRequest, UserProfileResponseDTO profileDTO) {

        return GatheringRequestDetailResDTO.builder()
                .gatheringRequestId(gatheringRequest.getId())
                .title(gatheringRequest.getGathering().getGatheringInfo().getTitle())
                .reqNickName(gatheringRequest.getRequestUser().getProfile().getNickname())
                .reqProfileImgUrl(profileDTO.getProfileImgUrl())
                .reqZamfitTest(profileDTO.getZamfitTest())
                .reqAgeGroupGender(profileDTO.getProfileImgUrl() + " " + profileDTO.getUserGender())
                .reqStateCity(profileDTO.getState() + " " + profileDTO.getCity())
                .hostName(gatheringRequest.getGathering().getLeader().getProfile().getNickname())
                .reqReason(gatheringRequest.getRequestReason())
                .gatheringRequestStatus(gatheringRequest.getGatheringRequestStatus())
                .build();

    }
}
