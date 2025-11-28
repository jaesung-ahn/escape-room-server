package com.wiiee.server.api.application.gathering.favorite;

import lombok.Getter;
import lombok.Value;

import jakarta.validation.constraints.NotNull;

@Getter
public class GatheringFavoritePostRequestDTO {

    @NotNull(message = "동행모집 번호를 입력하세요.")
    Long gatheringId;

    public GatheringFavoritePostRequestDTO() {
    }

    public GatheringFavoritePostRequestDTO(Long gatheringId) {
        this.gatheringId = gatheringId;
    }
}
