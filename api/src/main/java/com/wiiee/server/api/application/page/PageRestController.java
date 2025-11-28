package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.content.ContentGetRequestDTO;
import com.wiiee.server.api.application.content.ContentOrderType;
import com.wiiee.server.api.application.content.review.ReviewGetRequestDTO;
import com.wiiee.server.api.application.event.EventModel;
import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.api.application.gathering.GatheringModel;
import com.wiiee.server.api.application.gathering.comment.MultipleCommentModel;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.content.favorite.ContentFavoriteService;
import com.wiiee.server.api.domain.content.review.ReviewService;
import com.wiiee.server.api.domain.event.EventService;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.gathering.comment.CommentService;
import com.wiiee.server.api.domain.gathering.favorite.GatheringFavoriteService;
import com.wiiee.server.api.domain.recommendation.RecommendationService;
import com.wiiee.server.api.domain.recommendation.WbtiRecommendationService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.event.EventLocation;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Page api")

@RequiredArgsConstructor
@RequestMapping("/api/page")
@RestController
public class PageRestController {

    private final ContentService contentService;
    private final EventService eventService;
    private final ReviewService reviewService;
    private final GatheringService gatheringService;
    private final CommentService commentService;
    private final WbtiRecommendationService wbtiRecommendationService;
    private final RecommendationService recommendationService;
    private final ContentFavoriteService contentFavoriteService;
    private final GatheringFavoriteService gatheringFavoriteService;
    private final UserService userService;

    @Operation(summary = "메인 페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/main", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MainPageModel> getMain(@Parameter(hidden = true) @AuthUser User user) {

        List<EventModel> eventModelList = eventService.getAllEvents();

        final var mainEvents = eventService.getEventModelByEventLocation(eventModelList, EventLocation.MAIN_BANNER);
        final var recommendContents = contentService.getRecommendContents(user.getId());
        final var topEvents = eventService.getEventModelByEventLocation(eventModelList, EventLocation.ROTATION_BANNER1);
        final var myPlaceContents = contentService.getContentsByContentGetRequestDTO(
                ContentGetRequestDTO.builder()
                        .cityCode(user.getProfile().getCity().getCode())
                        .contentOrderType(ContentOrderType.RATING).build()
        );
        final var hotContents = contentService.getMainHotContentList();
        final var realTimeReviews = reviewService.getReviews(
                user.getId(),
                ReviewGetRequestDTO.builder().build()
        );
        final var bottomEvents = eventService.getEventModelByEventLocation(eventModelList, EventLocation.ROTATION_BANNER2);

        return ApiResponse.success(MainPageModel.of(
                mainEvents,
                recommendContents.getContents(),
                topEvents,
                myPlaceContents.getContents(),
                hotContents,
                realTimeReviews.getReviews(),
                bottomEvents));
    }

    @Operation(summary = "상품 상세 페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/content/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ContentDetailPageModel> getMain(@PathVariable("id") Long contentId,
                                                       @Parameter(hidden = true) @AuthUser User user) {
        final var content = contentService.getContent(contentId);
        final var reviews = reviewService.getReviewsByContentId(user.getId(), contentId, ReviewGetRequestDTO.builder().build());
        final var otherContents = contentService.getContentsByContentGetRequestDTO(
                ContentGetRequestDTO.builder()
                        .from(LocalDateTimeUtil.getLocalDateTimeNow().minusMonths(3))
                        .exceptContentId(contentId)
                        .contentOrderType(ContentOrderType.LATEST).build()
        );
        final var similarContents = contentService.getContentsByContentGetRequestDTO(
                ContentGetRequestDTO.builder()
                        .from(LocalDateTimeUtil.getLocalDateTimeNow().minusMonths(3))
                        .exceptContentId(contentId)
                        .contentOrderType(ContentOrderType.LATEST).build()
        );
        final var recommendGatherings = gatheringService.getGatherings(
                GatheringGetRequestDTO.builder().build()
        );

        return ApiResponse.success(ContentDetailPageModel.of(
                content,
                reviews.getReviews(),
                otherContents.getContents(),
                similarContents.getContents(),
                recommendGatherings.getGatherings(),
                contentFavoriteService.getFavorite(contentId, user.getId())
        ));
    }


    @Operation(summary = "내가 좋아할 만한 추천 상세 페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/recommend", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<RecommendDetailPageModel> getRecommendDetail(@Validated @ModelAttribute ContentGetRequestDTO dto,
                                                                    @Parameter(hidden = true) @AuthUser User user) {
        final var wbtiContents = contentService.getWbtiRecommendationContentsByUserId(user.getId());
        final var category = recommendationService.getRecommendationModel();
        final var contents = contentService.getContentsByContentGetRequestDTO(dto);

        return ApiResponse.success(RecommendDetailPageModel.of(
                wbtiContents.getContents(),
                category,
                contents.getContents()
        ));
    }

    @Operation(summary = "동행모집 상세 페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/gathering/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringDetailPageModel> getGatheringDetailPageModel(@PathVariable("id") Long gatheringId,
                                                                             @Parameter(hidden = true) @AuthUser User user) {
        GatheringModel gathering = gatheringService.getGatheringDetail(gatheringId, user.getId());
        MultipleCommentModel comments = commentService.getComments(gatheringId, user.getId());

        return ApiResponse.success(GatheringDetailPageModel.of(
                gathering,
                comments,
                gatheringFavoriteService.getFavorite(gatheringId, user.getId())
        ));
    }

    @Operation(summary = "유저 상세 페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<UserDetailPageModel> getUserDetailPageModel(@PathVariable("id") Long userId,
                                                                   @Parameter(hidden = true) @AuthUser User user) {
        final var myPage = userService.getMyPage(userId);
        final var reviews = reviewService.getReviewsByUserId(userId, PageRequestDTO.of(1, 5));
        final var gatherings = gatheringService.getMyGatheringList(userId);
        final var contentFavorites = contentFavoriteService.getMyFavoritesWithContent(userId, PageRequestDTO.of(1, 5));


        return ApiResponse.success(UserDetailPageModel.of(myPage,
                reviews.getReviews(),
                gatherings,
                contentFavorites.getContents()));
    }
}