package com.wiiee.server.api.application.content.favorite;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import lombok.Value;

import java.util.List;

@Value
public class MultipleContentFavoriteModel {

    List<ContentSimpleModel> contents;
    Long count;
    Boolean hasNext;

    public static MultipleContentFavoriteModel fromContentFavorites(List<ContentSimpleModel> contents, Long count, Boolean hasNext) {
        return new MultipleContentFavoriteModel(contents, count, hasNext);
    }
}
