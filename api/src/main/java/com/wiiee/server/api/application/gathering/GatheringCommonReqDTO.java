package com.wiiee.server.api.application.gathering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GatheringCommonReqDTO {

    @Schema(description = "동행모집 아이디")
    Long gatheringId;

    @Builder
    public GatheringCommonReqDTO(Long gatheringId) {
        this.gatheringId = gatheringId;
    }
}
