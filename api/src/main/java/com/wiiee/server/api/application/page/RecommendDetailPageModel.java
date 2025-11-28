package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.recommendation.RecommendationModel;
import lombok.Value;

import java.util.List;

@Value
public class RecommendDetailPageModel {

    List<ContentSimpleModel> wbtiContents;
    RecommendationModel category;
    List<ContentSimpleModel> etcContents;

    public static RecommendDetailPageModel of(List<ContentSimpleModel> wbtiContents, RecommendationModel category, List<ContentSimpleModel> etcContents) {
        return new RecommendDetailPageModel(
                wbtiContents,
                category,
                etcContents);
    }
}
