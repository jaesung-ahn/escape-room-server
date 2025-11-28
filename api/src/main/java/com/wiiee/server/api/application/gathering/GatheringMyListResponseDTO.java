package com.wiiee.server.api.application.gathering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;


@Value
@ToString
public class GatheringMyListResponseDTO {

    @Schema(description = "참여중 동행모집")
    List<GatheringListModel> ingList;
    @Schema(description = "개설 동행모집")
    List<GatheringListModel> createdList;
    @Schema(description = "종료 동행모집")
    List<GatheringListModel> endedList;

    @Builder
    public GatheringMyListResponseDTO(List<GatheringListModel> ingList, List<GatheringListModel> createdList, List<GatheringListModel> endedList) {
        this.ingList = ingList;
        this.createdList = createdList;
        this.endedList = endedList;
    }
    //    @Schema(description = "동행모집 신청 상태")
//    GatheringRequestStatus gatheringRequestStatus;

//    public static GatheringMyListResponseDTO fromGatheringRequestDetail(GatheringRequest gatheringRequest, UserProfileResponseDTO profileDTO) {
//
//        return GatheringMyListResponseDTO.builder()
//                .gatheringRequestId(gatheringRequest.getId())
//                .title(gatheringRequest.getGathering().getGatheringInfo().getTitle())
//                .reqNickName(gatheringRequest.getRequestUser().getProfile().getNickname())
//                .reqProfileImgUrl(profileDTO.getProfileImgUrl())
//                .reqZamfitTest(profileDTO.getZamfitTest())
//                .reqAgeGroupGender(profileDTO.getProfileImgUrl() + " " + profileDTO.getUserGender())
//                .reqStateCity(profileDTO.getState() + " " + profileDTO.getCity())
//                .hostName(gatheringRequest.getGathering().getLeader().getProfile().getNickname())
//                .reqReason(gatheringRequest.getRequestReason())
//                .gatheringRequestStatus(gatheringRequest.getGatheringRequestStatus())
//                .build();
//
//    }
}
