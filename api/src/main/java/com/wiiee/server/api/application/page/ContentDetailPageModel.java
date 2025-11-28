package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.content.ContentModel;
import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.content.favorite.ContentFavoriteModel;
import com.wiiee.server.api.application.content.review.ReviewSimpleModel;
import com.wiiee.server.api.application.gathering.GatheringSimpleModel;
import lombok.Value;

import java.util.List;

@Value
public class ContentDetailPageModel {

    ContentModel content;
    ContentFavoriteModel favorite;
    List<ReviewSimpleModel> reviews;
    List<ContentSimpleModel> otherContents;
    List<ContentSimpleModel> similarContents;
    List<GatheringSimpleModel> recommendGatherings;

    public static ContentDetailPageModel of(ContentModel content, List<ReviewSimpleModel> reviews, List<ContentSimpleModel> otherContents, List<ContentSimpleModel> similarContents, List<GatheringSimpleModel> recommendGatherings, ContentFavoriteModel favorite) {
        return new ContentDetailPageModel(
                content,
                favorite,
                reviews,
                otherContents,
                similarContents,
                recommendGatherings
        );
    }

}
