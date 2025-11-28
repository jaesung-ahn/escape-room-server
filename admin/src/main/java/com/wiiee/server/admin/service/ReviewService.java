package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.ReviewDetailForm;
import com.wiiee.server.admin.form.ReviewListForm;
import com.wiiee.server.admin.repository.ReviewRepository;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.content.review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<ReviewListForm> findAllForForm() {
        List<ReviewListForm> reviewListForms = new ArrayList<>();

        reviewRepository.findAll().forEach(review -> {
            ReviewListForm reviewListForm = new ReviewListForm();
            reviewListForm.setId(review.getId());
            reviewListForm.setContentName(review.getContent().getContentBasicInfo().getName());
            reviewListForm.setMessage(review.getMessage());
            reviewListForm.setWriter(review.getWriter().getProfile().getNickname());
            reviewListForm.setRating(review.getRating());
            reviewListForm.setCreatedAt(review.getCreatedAt());
            reviewListForm.setIsApproval(review.isApproval());
            reviewListForms.add(reviewListForm);
        });

        return reviewListForms;
    }

    @Transactional(readOnly = true)
    public ReviewDetailForm findByIdForForm(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).get();
        ReviewDetailForm reviewDetailForm = new ReviewDetailForm();
        reviewDetailForm.setId(review.getId());
        reviewDetailForm.setMessage(review.getMessage());
        reviewDetailForm.setWriter(review.getWriter().getProfile().getNickname());
        reviewDetailForm.setContentName(review.getContent().getContentBasicInfo().getName());
        reviewDetailForm.setRating(review.getRating());
        reviewDetailForm.setJoinNumber(review.getJoinNumber());
        reviewDetailForm.setIsApproval(review.isApproval());
        reviewDetailForm.setRealGatherDate(review.getRealGatherDate());
        reviewDetailForm.setCreatedAt(review.getCreatedAt());

        reviewDetailForm.setReviewImages(
            imageService.findByIdsIn(review.getImageIds()).stream().map(Image::getUrl).collect(Collectors.toList())
        );

        return reviewDetailForm;
    }

    @Transactional
    public void updateReview(ReviewListForm reviewListForm) {
        Review review = reviewRepository.findById(reviewListForm.getId()).get();
        review.updateApproval(reviewListForm.getIsApproval());
        reviewRepository.save(review);
    }
}
