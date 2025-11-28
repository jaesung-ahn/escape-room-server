package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.content.review.ReviewSimpleModel;
import com.wiiee.server.api.application.gathering.GatheringMyListResponseDTO;
import com.wiiee.server.api.application.user.UserMypageResponseDTO;
import lombok.Value;

import java.util.List;

@Value
public class UserDetailPageModel {

    UserMypageResponseDTO user;
    List<ReviewSimpleModel> reviews;
    GatheringMyListResponseDTO gatherings;
    List<ContentSimpleModel> contents;

    public static UserDetailPageModel of(UserMypageResponseDTO user, List<ReviewSimpleModel> reviews, GatheringMyListResponseDTO gatherings, List<ContentSimpleModel> contents) {
        return new UserDetailPageModel(user, reviews, gatherings, contents);
    }
}
