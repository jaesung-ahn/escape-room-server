package com.wiiee.server.api.application.content.review;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.content.review.ReviewService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Review api")

@RequiredArgsConstructor
@RequestMapping("/api/review")
@RestController
public class ReviewRestController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/content/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ReviewModel> postReview(@Parameter(hidden = true) @AuthUser User user,
                                               @PathVariable("id") Long contentId,
                                               @Validated @RequestBody ReviewPostRequestDTO dto) {
        return ApiResponse.success(reviewService.createReview(user.getId(), contentId, dto));
    }

    @Operation(summary = "단일 리뷰 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ReviewModel> getReview(@Parameter(hidden = true) @AuthUser User user,
                                              @PathVariable("id") Long reviewId) {
        return ApiResponse.success(reviewService.getReview(user.getId(), reviewId));
    }

    @Operation(summary = "리뷰 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/content/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleReviewModel> getReviewsByContentId(@Parameter(hidden = true) @AuthUser User user,
                                                                  @PathVariable("id") Long contentId,
                                                                  @Validated @ModelAttribute ReviewGetRequestDTO dto) {
        return ApiResponse.success(reviewService.getReviewsByContentId(user.getId(), contentId, dto));
    }

    @Operation(summary = "리뷰 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleReviewModel> getReviews(@Parameter(hidden = true) @AuthUser User user,
                                                       @Validated @ModelAttribute ReviewGetRequestDTO dto) {
        return ApiResponse.success(reviewService.getReviews(user.getId(), dto));
    }

    @Operation(summary = "리뷰 수정", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ReviewModel> putReview(@Parameter(hidden = true) @AuthUser User user,
                                              @PathVariable("id") Long reviewId,
                                              @Validated @RequestBody ReviewPutRequestDTO dto) {
        return ApiResponse.success(reviewService.updateReview(user.getId(), reviewId, dto));
    }

    @Operation(summary = "리뷰 삭제", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> deleteReview(@Parameter(hidden = true) @AuthUser User user,
                                       @PathVariable("id") Long reviewId) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 리뷰 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/my-page", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleReviewModel> getMyReviews(@Parameter(hidden = true) @AuthUser User user,
                                                         @ModelAttribute PageRequestDTO dto) {
        return ApiResponse.success(reviewService.getReviewsByUserId(user.getId(), dto));
    }
}