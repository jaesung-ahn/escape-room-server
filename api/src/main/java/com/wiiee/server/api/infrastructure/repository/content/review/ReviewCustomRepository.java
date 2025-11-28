package com.wiiee.server.api.infrastructure.repository.content.review;

import com.wiiee.server.api.application.content.review.ReviewGetRequestDTO;
import com.wiiee.server.api.application.review.ReviewStatInfo;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

    /**
     * 리뷰 목록 조회
     */
    Page<Review> findAllByReviewGetRequestDTO(User user, Long contentId, ReviewGetRequestDTO dto);

    /**
     * 놀거리의 리뷰 평점과 리뷰 개수 조회
     */
    ReviewStatInfo findReviewAvgAndCountByContent(Long contentId);

}
