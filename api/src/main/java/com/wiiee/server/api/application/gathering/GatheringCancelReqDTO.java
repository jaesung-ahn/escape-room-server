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
public class GatheringCancelReqDTO {

    @Schema(description = "동행모집 신청서 아이디")
    Long gatheringRequestId;

    @Builder
    public GatheringCancelReqDTO(Long gatheringRequestId) {
        this.gatheringRequestId = gatheringRequestId;
    }
}
