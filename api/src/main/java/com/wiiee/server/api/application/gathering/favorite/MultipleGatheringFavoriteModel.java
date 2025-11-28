package com.wiiee.server.api.application.gathering.favorite;

import lombok.Value;

import java.util.List;

@Value
public class MultipleGatheringFavoriteModel {

    List<GatheringFavoriteSimpleModel> gatheringFavorites;
    Long count;
    Boolean hasNext;

    public static MultipleGatheringFavoriteModel fromGatheringFavorites(List<GatheringFavoriteSimpleModel> gatheringFavorites, Long count, Boolean hasNext) {
        return new MultipleGatheringFavoriteModel(gatheringFavorites, count, hasNext);
    }
}
