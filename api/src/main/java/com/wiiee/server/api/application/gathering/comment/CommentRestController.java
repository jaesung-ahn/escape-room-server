package com.wiiee.server.api.application.gathering.comment;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.gathering.comment.CommentService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Comment api")

@RequiredArgsConstructor
@RequestMapping("/api/comment")
@RestController
public class CommentRestController {

    private final CommentService commentService;

    @Operation(summary = "동행 모집 댓글 등록", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/gathering/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<CommentModel> postComment(@PathVariable("id") Long gatheringId,
                                                 @RequestBody CommentPostRequestDTO dto,
                                                 @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(commentService.createComment(gatheringId, user.getId(), dto));
    }

    @Operation(summary = "동행모집 댓글 목록 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/gathering/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleCommentModel> getComments(@PathVariable("id") Long gatheringId,
                                                         @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(commentService.getComments(gatheringId, user.getId()));
    }

    @Operation(summary = "동행 모집 댓글 수정", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<CommentModel> putComment(@PathVariable("id") Long commentId,
                                                @RequestBody CommentPutRequestDTO dto,
                                                @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(commentService.update(commentId, user.getId(), dto));
    }

    @Operation(summary = "동행 모집 댓글 삭제", security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable("id") Long commentId,
                                           @Parameter(hidden = true) @AuthUser User user) {
        commentService.delete(commentId, user.getId());
        return ApiResponse.successWithNoData();
    }
}
