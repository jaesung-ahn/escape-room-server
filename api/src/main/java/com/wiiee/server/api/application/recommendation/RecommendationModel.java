package com.wiiee.server.api.application.recommendation;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import lombok.Value;

import java.util.List;


@Value
public class RecommendationModel {

    String categoryName;
    List<ContentSimpleModel> contents;

    public static RecommendationModel fromRecommendationAndContentSimpleModels(String categoryName, List<ContentSimpleModel> contents) {
        return new RecommendationModel(categoryName,
                contents);
    }

}
