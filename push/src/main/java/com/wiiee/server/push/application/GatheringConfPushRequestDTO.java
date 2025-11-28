package com.wiiee.server.push.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class GatheringConfPushRequestDTO {

    @Schema(required = true, description = "gathering id")
    private Long gatheringId;

    @Schema(required = true, description = "동행 신청자 id")
    private Long userId;

    @Schema(required = true, description = "2: 동행 수락, 3: 동행 거절")
    private int confirmCode;

    @Builder
    public GatheringConfPushRequestDTO(Long gatheringId, Long userId, int confirmCode) {
        this.gatheringId = gatheringId;
        this.userId = userId;
        this.confirmCode = confirmCode;
    }
}
