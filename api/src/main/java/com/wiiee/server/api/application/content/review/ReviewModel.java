package com.wiiee.server.api.application.content.review;

import com.wiiee.server.api.application.user.UserSimpleModel;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.content.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.List;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class ReviewModel {

    @Schema(description = "아이디")
    Long id;
    @Schema(description = "닉네임")
    String nickname;
    @Schema(description = "평점")
    Double rating;
    @Schema(description = "내용")
    String message;
    @Schema(description = "작성시간")
    String dateFormat;
    @Schema(description = "리뷰 작성자 유무")
    Boolean isOwner;
    @Schema(description = "이미지 url")
    List<String> imageUrls;

    public static ReviewModel fromReviewAndImages(Review review, Long userId, List<Image> images) {

        UserSimpleModel user = UserSimpleModel.fromUser(review.getWriter());

        return ReviewModel.builder()
                .id(review.getId())
                .nickname(user.getNickname())
                .rating(review.getRating())
                .message(review.getMessage())
                .dateFormat(LocalDateTimeUtil.getDateFormat(review.getCreatedAt()))
                .isOwner(user.getId().equals(userId))
                .imageUrls(images.stream().map(Image::getUrl).collect(toList()))
                .build();
    }

}
