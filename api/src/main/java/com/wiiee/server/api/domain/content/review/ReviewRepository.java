package com.wiiee.server.api.domain.content.review;

import com.wiiee.server.api.application.content.review.ReviewGetRequestDTO;
import com.wiiee.server.api.infrastructure.repository.content.review.ReviewCustomRepository;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    @Override
    Page<Review> findAllByReviewGetRequestDTO(User user, Long contentId, ReviewGetRequestDTO dto);

}
