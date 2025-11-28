package com.wiiee.server.api.application.content.favorite;

import lombok.Getter;

@Getter
public class ContentFavoriteModel {

    private Integer count;
    private Boolean isFavorite;

    public ContentFavoriteModel(Integer count, Boolean isFavorite) {
        this.count = count;
        this.isFavorite = isFavorite;
    }

    public static ContentFavoriteModel of(Integer count, Boolean isFavorite) {
        return new ContentFavoriteModel(count, isFavorite);
    }

}
