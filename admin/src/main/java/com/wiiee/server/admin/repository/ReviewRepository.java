package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.content.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
