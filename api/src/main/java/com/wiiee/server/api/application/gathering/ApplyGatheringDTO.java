package com.wiiee.server.api.application.gathering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplyGatheringDTO {

    @Schema(description = "동행모집 아이디")
    Long gatheringId;

    @Schema(description = "동행모집 신청 내용")
    String requestReason;

}
