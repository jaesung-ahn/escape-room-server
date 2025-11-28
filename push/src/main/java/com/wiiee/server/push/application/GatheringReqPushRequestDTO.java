package com.wiiee.server.push.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class GatheringReqPushRequestDTO {

    @Schema(required = true, description = "동행모집 id")
    private Long gatheringId;

    @Schema(required = true, description = "동행 신청서 id")
    private Long gatheringMemberId;

    @Schema(required = true, description = "동행 리더 id")
    private Long leaderId;

    @Schema(required = true, description = "동행 신청자 id")
    private Long userId;

    @Builder
    public GatheringReqPushRequestDTO(Long gatheringId, Long gatheringMemberId, Long leaderId, Long userId) {
        this.gatheringId = gatheringId;
        this.gatheringMemberId = gatheringMemberId;
        this.leaderId = leaderId;
        this.userId = userId;
    }
}
