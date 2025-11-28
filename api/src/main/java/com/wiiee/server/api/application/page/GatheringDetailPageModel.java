package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.gathering.GatheringModel;
import com.wiiee.server.api.application.gathering.comment.CommentModel;
import com.wiiee.server.api.application.gathering.comment.MultipleCommentModel;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteModel;
import lombok.Value;

import java.util.List;

@Value
public class GatheringDetailPageModel {

    GatheringModel gatheringModel;
    GatheringFavoriteModel gatheringFavoriteModel;
    MultipleCommentModel multipleCommentModel;

    public static GatheringDetailPageModel of(GatheringModel gatheringModel, MultipleCommentModel comments, GatheringFavoriteModel gatheringFavoriteModel) {
        return new GatheringDetailPageModel(
                gatheringModel,
                gatheringFavoriteModel,
                comments
        );
    }

}
