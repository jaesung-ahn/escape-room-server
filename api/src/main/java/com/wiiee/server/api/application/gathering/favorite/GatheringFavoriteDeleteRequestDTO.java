package com.wiiee.server.api.application.gathering.favorite;

import lombok.Getter;
import lombok.Value;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
public class GatheringFavoriteDeleteRequestDTO {

    @Getter
    public static class Single {
        @NotNull(message = "동행모집 번호를 입력하세요.")
        Long gatheringId;

        public Single(Long gatheringId) {
            this.gatheringId = gatheringId;
        }

        protected Single() {
        }
    }

    @Getter
    public static class Multi {
        @NotNull(message = "동행모집 번호를 입력하세요.")
        List<Long> gatheringIds;

        public Multi(List<Long> gatheringIds) {
            this.gatheringIds = gatheringIds;
        }

        protected Multi() {
        }
    }

}
