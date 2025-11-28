package com.wiiee.server.api.application.gathering;

import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GatheringConfirmReqDTO {

    @Schema(description = "동행모집 신청서 아이디")
    Long gatheringRequestId;

    @Schema(description = "변경할 동행모집 신청서 상태")
    GatheringRequestStatus gatheringRequestStatus;

    @Builder
    public GatheringConfirmReqDTO(Long gatheringRequestId, GatheringRequestStatus gatheringRequestStatus) {
        this.gatheringRequestId = gatheringRequestId;
        this.gatheringRequestStatus = gatheringRequestStatus;
    }
    //    public static GatheringConfirmReqDTO fromGatheringConfirmReqDTO(Long gatheringRequestId, GatheringRequestStatus gatheringRequestStatus) {
//
//        return GatheringConfirmReqDTO.builder()
//                .gatheringRequestId(gatheringRequestId)
//                .gatheringRequestStatus(gatheringRequestStatus)
//                .build();
//    }
}
