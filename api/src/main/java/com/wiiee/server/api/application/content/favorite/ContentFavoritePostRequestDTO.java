package com.wiiee.server.api.application.content.favorite;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;

@Getter
public class ContentFavoritePostRequestDTO {

    @NotNull(message = "컨텐츠 번호를 입력하세요.")
    Long contentId;

    public ContentFavoritePostRequestDTO() {
    }

    public ContentFavoritePostRequestDTO(Long contentId) {
        this.contentId = contentId;
    }
}
