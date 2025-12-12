package com.wiiee.server.api.application.content;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.content.favorite.ContentFavoriteDeleteRequestDTO;
import com.wiiee.server.api.application.content.favorite.ContentFavoriteModel;
import com.wiiee.server.api.application.content.favorite.ContentFavoritePostRequestDTO;
import com.wiiee.server.api.application.content.favorite.MultipleContentFavoriteModel;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.content.favorite.ContentFavoriteService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Content api")

@RequiredArgsConstructor
@RequestMapping("/api/content")
@RestController
public class ContentRestController {

    private final ContentService contentService;
    private final ContentFavoriteService contentFavoriteService;

    @Operation(summary = "[테스트용] 컨텐츠 생성", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ContentModel> postContent(@Validated @RequestBody ContentPostRequestDTO dto) {
        return ApiResponse.success(contentService.createNewContent(dto));
    }

    @Operation(summary = "컨텐츠 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleContentModel> getContents(@Validated @ModelAttribute ContentGetRequestDTO dto) {
        return ApiResponse.success(contentService.getContentsByContentGetRequestDTO(dto));
    }

    @Operation(summary = "컨텐츠 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/{id}")
    public ApiResponse<ContentModel> getContent(@Parameter(hidden = true) @AuthUser User user,
                                                @PathVariable("id") Long id) {
        return ApiResponse.success(contentService.getContent(id));
    }

    @Operation(summary = "내가 좋아할 만한 추천", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/recommend", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleContentModel> getRecommendContents(@Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(contentService.getRecommendContents(user.getId()));
    }

    @Operation(summary = "컨텐츠 찜 등록", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/favorite", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ContentFavoriteModel> postContentFavorite(@Parameter(hidden = true) @AuthUser User user,
                                                                 @RequestBody @Validated ContentFavoritePostRequestDTO dto) {
        return ApiResponse.success(contentFavoriteService.addFavorite(dto.getContentId(), user.getId()));
    }

    @Operation(summary = "컨텐츠 찜 삭제(단건)", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/favorite", consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<ContentFavoriteModel> deleteContentFavorite(@Parameter(hidden = true) @AuthUser User user,
                                                                   @RequestBody @Validated ContentFavoriteDeleteRequestDTO.Single dto) {
        return ApiResponse.success(contentFavoriteService.deleteFavorite(dto.getContentId(), user.getId()));
    }

    @Operation(summary = "컨텐츠 찜 삭제(복수)", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/favorite/multi", consumes = APPLICATION_JSON_VALUE)
    public ApiResponse<Void> deleteContentFavorites(@Parameter(hidden = true) @AuthUser User user,
                                                    @RequestBody @Validated ContentFavoriteDeleteRequestDTO.Multi dto) {
        contentFavoriteService.deleteFavorite(dto.getContentIds(), user.getId());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내 컨텐츠 찜리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/favorite", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleContentFavoriteModel> getMyFavoritesWithContent(@Parameter(hidden = true) @AuthUser User user,
                                                                               @ModelAttribute PageRequestDTO dto) {
        return ApiResponse.success(contentFavoriteService.getMyFavoritesWithContent(user.getId(), dto));
    }
}
