package com.wiiee.server.api.application.gathering.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

@Getter
public class CommentPostRequestDTO {

    @Schema(description = "부모 댓글 아이디")
    private Long parentCommentId;
    @Schema(description = "댓글 내용")
    @NotBlank(message = "메세지를 입력하세요.")
    private String message;

    protected CommentPostRequestDTO() {
    }

    public CommentPostRequestDTO(Long parentCommentId, String message) {
        this.parentCommentId = parentCommentId;
        this.message = message;
    }

    public CommentPostRequestDTO(String message) {
        this.message = message;
    }
}
