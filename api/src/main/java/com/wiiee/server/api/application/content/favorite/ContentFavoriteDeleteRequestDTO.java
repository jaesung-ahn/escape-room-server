package com.wiiee.server.api.application.content.favorite;

import lombok.Getter;
import lombok.Value;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
public class ContentFavoriteDeleteRequestDTO {

    @Getter
    public static class Single {
        @NotNull(message = "컨텐츠 번호를 입력하세요.")
        Long contentId;

        public Single(Long contentId) {
            this.contentId = contentId;
        }

        protected Single() {
        }
    }

    @Getter
    public static class Multi {
        @NotNull(message = "컨텐츠 번호를 입력하세요.")
        List<Long> contentIds;

        public Multi(List<Long> contentIds) {
            this.contentIds = contentIds;
        }

        protected Multi() {
        }
    }
}
