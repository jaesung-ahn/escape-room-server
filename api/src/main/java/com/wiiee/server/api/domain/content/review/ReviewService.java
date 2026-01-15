package com.wiiee.server.api.domain.content.review;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.content.review.*;
import com.wiiee.server.api.application.exception.CustomException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.domain.code.ReviewErrorCode;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.slack.SlackService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.review.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserService userService;
    private final ContentService contentService;
    private final ImageService imageService;

    private final ReviewRepository reviewRepository;

    private final SlackService slackService;

    @Transactional
    public ReviewModel createReview(Long userId, Long contentId, ReviewPostRequestDTO dto) {
        String message = dto.getMessage();
        final var user = userService.findById(userId);
        final var content = contentService.findById(contentId).orElseThrow();
        final var reviewAdded = user.addReviewToContent(content, message, dto.getRating(), dto.getJoinNumber(),
                dto.getImageIds(), dto.getRealGatherDate());

        try {
            slackService.sendSlackMessage(content.getContentBasicInfo().getName(), message);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        return ReviewModel.fromReviewAndImages(reviewAdded, userId, imageService.findByIdsIn(reviewAdded.getImageIds()));
    }

    public MultipleReviewModel getReviewsByContentId(Long userId, Long contentId, ReviewGetRequestDTO dto) {
        final var list = reviewRepository.findAllByReviewGetRequestDTO(null, contentId, dto);
        return MultipleReviewModel.fromReviews(getReviewSimpleModelByReviewsAndUserId(userId, list.getContent()), list);
    }

    public MultipleReviewModel getReviews(Long userId, ReviewGetRequestDTO dto) {
        final var list = reviewRepository.findAllByReviewGetRequestDTO(null, null, dto);
        return MultipleReviewModel.fromReviews(getReviewSimpleModelByReviewsAndUserId(userId, list.getContent()), list);
    }

    public MultipleReviewModel getReviewsByUserId(Long userId, PageRequestDTO dto) {
        final var user = userService.findById(userId);
        final var list = reviewRepository.findAllByReviewGetRequestDTO(user, null, new ReviewGetRequestDTO(null, null, dto));
        return MultipleReviewModel.fromReviews(getReviewSimpleModelByReviewsAndUserId(userId, list.getContent()), list);
    }

    public ReviewModel getReview(Long userId, Long reviewId) {
        final var findReview = reviewRepository.getById(reviewId);
        return ReviewModel.fromReviewAndImages(findReview, userId, imageService.findByIdsIn(findReview.getImageIds()));
    }

    @Transactional
    public ReviewModel updateReview(Long userId, Long reviewId, ReviewPutRequestDTO dto) {
        final var user = userService.findById(userId);
        final var targetReview = reviewRepository.findById(reviewId).orElseThrow();
        if (!targetReview.getWriter().equals(user)) {
            throw new ForbiddenException(ReviewErrorCode.ERROR_REVIEW_UPDATE_PERMISSION_DENIED);
        }
        targetReview.updateReview(dto.getMessage(), dto.getRating(), dto.getImageIds());
        return ReviewModel.fromReviewAndImages(targetReview, userId, imageService.findByIdsIn(targetReview.getImageIds()));
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        final var user = userService.findById(userId);
        final var targetReview = reviewRepository.findById(reviewId).orElseThrow();
        if (!targetReview.getWriter().equals(user)) {
            throw new ForbiddenException(ReviewErrorCode.ERROR_REVIEW_DELETE_PERMISSION_DENIED);
        }
        reviewRepository.deleteById(reviewId);
    }

    private List<ReviewSimpleModel> getReviewSimpleModelByReviewsAndUserId(Long userId, List<Review> reviews) {
        return reviews.stream().
                map(review -> ReviewSimpleModel.fromReviewAndImage(review, userId,
                        imageService.getImageById(review.getRepresentativeImageId()),
                        imageService.getImageById(Optional.ofNullable(review.getWriter().getProfile().getProfileImageId()).orElse(0L)))
                        )
                .collect(toList());
    }

    // 리뷰 승인처리 테스트용으로 만든 서비스, 실제로는 어드민에서만 동작해야함
    @Transactional
    public void updateApprovalReview(Long userId, Long reviewId) {
        final var user = userService.findById(userId);
        final var targetReview = reviewRepository.findById(reviewId).orElseThrow();
        if (!targetReview.getWriter().equals(user)) {
            throw new CustomException(8124, "해당 리뷰 수정 권한이 없습니다.", null);
        }

        targetReview.updateApproval(true);
    }

}
