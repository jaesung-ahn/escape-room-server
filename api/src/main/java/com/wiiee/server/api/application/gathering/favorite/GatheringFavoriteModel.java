package com.wiiee.server.api.application.gathering.favorite;

import lombok.Getter;

@Getter
public class GatheringFavoriteModel {

    private Integer count;
    private Boolean isFavorite;

    public GatheringFavoriteModel(Integer count, Boolean isFavorite) {
        this.count = count;
        this.isFavorite = isFavorite;
    }

    public static GatheringFavoriteModel of(Integer count, Boolean isFavorite) {
        return new GatheringFavoriteModel(count, isFavorite);
    }

}
