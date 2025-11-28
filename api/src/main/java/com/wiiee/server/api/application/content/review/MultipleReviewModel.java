package com.wiiee.server.api.application.content.review;

import com.wiiee.server.common.domain.content.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class MultipleReviewModel {

    @Schema(description = "리뷰 리스트")
    List<ReviewSimpleModel> reviews;

    @Schema(description = "표출 개수")
    long count;

    @Schema(description = "다음 페이지 유무")
    boolean hasNext;

    public static MultipleReviewModel fromReviews(List<ReviewSimpleModel> reviews, Page<Review> reviewPage){
        return new MultipleReviewModel(reviews, reviewPage.getTotalElements(), reviewPage.hasNext());
    }

}
