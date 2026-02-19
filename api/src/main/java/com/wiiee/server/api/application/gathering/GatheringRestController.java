package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteDeleteRequestDTO;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteModel;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoritePostRequestDTO;
import com.wiiee.server.api.application.gathering.favorite.MultipleGatheringFavoriteModel;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.gathering.GatheringMemberService;
import com.wiiee.server.api.domain.gathering.GatheringRequestService;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.gathering.favorite.GatheringFavoriteService;
import com.wiiee.server.common.domain.user.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Gathering api")

@RequiredArgsConstructor
@RequestMapping("/api/gathering")
@RestController
public class GatheringRestController {

    private final GatheringService gatheringService;
    private final GatheringRequestService gatheringRequestService;
    private final GatheringMemberService gatheringMemberService;
    private final GatheringFavoriteService gatheringFavoriteService;

    @Operation(summary = "동행 모집 등록", security = {@SecurityRequirement(name = "Authorization")})
    @RateLimiter(name = "createGathering")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringModel> postGathering(@Validated @RequestBody GatheringPostRequestDTO dto,
                                                     @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(gatheringService.createNewGathering(dto, user.getId()));
    }

    @Operation(summary = "동행 모집 수정", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> updateGathering(@PathVariable("id") Long id,
                                          @Validated @RequestBody GatheringUpdateRequestDTO dto,
                                          @Parameter(hidden = true) @AuthUser User user) {
        gatheringService.updateGathering(dto, user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "동행 모집 상세 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringModel> getGatheringDetail(@PathVariable("id") Long id,
                                                          @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(gatheringService.getGatheringDetail(id, user.getId()));
    }

    @Operation(summary = "동행 모집 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleGatheringModel> getGatherings(@Validated @ModelAttribute GatheringGetRequestDTO dto) {
        return ApiResponse.success(gatheringService.getGatherings(dto));
    }

    @Operation(summary = "동행모집 참가 신청", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/apply", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> applyGathering(@Validated @RequestBody ApplyGatheringDTO dto,
                                         @Parameter(hidden = true) @AuthUser User user) {

        gatheringRequestService.applyGathering(dto.getGatheringId(), user.getId(), dto.getRequestReason());

        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "동행 모집 참가서 상세 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/requests/{requestId}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringRequestDetailResDTO> getGatheringRequestDetail(@PathVariable("requestId") Long requestId,
                                                                               @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(gatheringRequestService.getGatheringRequestDetail(requestId, user.getId()));
    }

    @Operation(summary = "동행모집 참가서 수락, 거절(호스트만)", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/confirm", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> confirmGatheringRequest(@Validated @RequestBody GatheringConfirmReqDTO dto,
                                                  @Parameter(hidden = true) @AuthUser User user) {

        gatheringRequestService.confirmGatheringRequest(dto, user.getId());

        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "동행모집 찜 등록", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/favorite", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringFavoriteModel> postGatheringFavorite(@Parameter(hidden = true) @AuthUser User user,
                                                                     @RequestBody @Validated GatheringFavoritePostRequestDTO dto) {
        return ApiResponse.success(gatheringFavoriteService.addFavorite(dto.getGatheringId(), user.getId()));
    }

    @Operation(summary = "동행모집 찜 삭제(단건)", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/favorite", consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringFavoriteModel> deleteGatheringFavorite(@Parameter(hidden = true) @AuthUser User user,
                                                                       @RequestBody @Validated GatheringFavoriteDeleteRequestDTO.Single dto) {
        return ApiResponse.success(gatheringFavoriteService.deleteFavorite(dto.getGatheringId(), user.getId()));
    }

    @Operation(summary = "동행모집 찜 삭제(복수)", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/favorite/multi", consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<Void> deleteGatheringFavorites(@Parameter(hidden = true) @AuthUser User user,
                                                      @RequestBody @Validated GatheringFavoriteDeleteRequestDTO.Multi dto) {
        gatheringFavoriteService.deleteFavorite(dto.getGatheringIds(), user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 동행모집 찜리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/favorite", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleGatheringFavoriteModel> getMyFavoritesWithGathering(@Parameter(hidden = true) @AuthUser User user,
                                                                                   @RequestParam Long userId,
                                                                                   @ModelAttribute PageRequestDTO dto) {
        return ApiResponse.success(gatheringFavoriteService.getMyFavoritesWithGathering(userId, dto));
    }

    @Operation(summary = "동행모집 참가서 취소(신청한 유저만)", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/cancel", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> cancelGatheringRequest(@Validated @RequestBody GatheringCancelReqDTO dto,
                                                 @Parameter(hidden = true) @AuthUser User user) {

        gatheringRequestService.cancelGatheringRequest(dto, user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 동행모집 목록", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/my-gatherings", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<GatheringMyListResponseDTO> getMyGatheringList(@Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(gatheringService.getMyGatheringList(user.getId()));
    }

    @Operation(summary = "내 동행모집 완료처리(호스트만)", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}/complete", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> completedGathering(@PathVariable("id") Long id,
                                             @Parameter(hidden = true) @AuthUser User user) {
        gatheringService.completedGathering(id, user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 동행모집 모집중으로 변경처리(호스트만)", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}/reopen", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> recruitingGathering(@PathVariable("id") Long id,
                                             @Parameter(hidden = true) @AuthUser User user) {
        gatheringService.recruitingGathering(id, user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 동행 참여 취소하기(신청한 유저만)", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/{id}/members", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> cancelJoinGathering(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthUser User user) {
        gatheringMemberService.cancelJoinGathering(id, user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 동행모집 삭제하기", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> deleteGathering(@PathVariable("id") Long id,
                                              @Parameter(hidden = true) @AuthUser User user) {
        gatheringService.deleteGathering(id, user.getId());
        return ApiResponse.successWithNoData();
    }
}
