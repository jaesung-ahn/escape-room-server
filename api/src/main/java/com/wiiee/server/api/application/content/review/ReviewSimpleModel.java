package com.wiiee.server.api.application.content.review;

import com.wiiee.server.api.application.user.UserSimpleModel;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.content.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class ReviewSimpleModel {

    @Schema(description = "아이디")
    Long id;
    @Schema(description = "유저 아이디")
    Long userId;
    @Schema(description = "닉네임")
    String nickname;

    @Schema(description = "프로필 이미지 url")
    String profileImageUrl;

    @Schema(description = "평점")
    Double rating;
    @Schema(description = "내용")
    String message;
    @Schema(description = "작성시간")
    String dateFormat;
    @Schema(description = "리뷰 작성자 유무")
    Boolean isOwner;
    @Schema(description = "참여 인원")
    Integer joinNumber;

    @Schema(description = "놀거리 이름")
    String contentName;
    @Schema(description = "업체 이름")
    String companyName;

    @Schema(description = "state")
    String stateName;
    @Schema(description = "city")
    String cityName;

    @Schema(description = "이미지 url")
    String imageUrl;

    public static ReviewSimpleModel fromReviewAndImage(Review review, Long userId, Image contentImage, Image userImage) {

        UserSimpleModel user = UserSimpleModel.fromUser(review.getWriter());

        return ReviewSimpleModel.builder()
                .id(review.getId())
                .userId(userId)
                .nickname(user.getNickname())
                .profileImageUrl(userImage.getUrl())
                .rating(review.getRating())
                .message(review.getMessage())
                .dateFormat(LocalDateTimeUtil.getDateFormat(review.getCreatedAt()))
                .isOwner(user.getId().equals(userId))
                .joinNumber(review.getJoinNumber())
                .contentName(review.getContent().getContentBasicInfo().getName())
                .companyName(review.getContent().getCompany().getBasicInfo().getName())
                .stateName(review.getContent().getCompany().getBasicInfo().getState().getName())
                .cityName(review.getContent().getCompany().getBasicInfo().getCity().getName())
                .imageUrl(contentImage.getUrl())
                .build();
    }

}
