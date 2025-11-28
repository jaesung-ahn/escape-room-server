package com.wiiee.server.api.application.gathering.comment;

import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.gathering.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentModel {

    @Schema(description = "댓글 번호")
    Long id;
    @Schema(description = "작성자")
    UserProfileResponseDTO writer;
    @Schema(description = "댓글 내용")
    String message;
    @Schema(description = "작성 시간")
    String createAt;
    @Schema(description = "작성 시간(포멧)")
    String dateFormat;
    @Schema(description = "메인 댓글 유무")
    Boolean isParent;
    @Schema(description = "댓글 작성자 유무")
    Boolean isOwner = false;
    @Schema(description = "하위 댓글")
    List<CommentModel> children = new ArrayList<>();

    @Schema(description = "댓글 삭제 유무")
    Boolean deleted = false;

    @Schema(description = "댓글 삭제 일시")
    String deletedAt;

    protected CommentModel() {
    }

    public CommentModel(Long id, UserProfileResponseDTO writer, String message, String createAt, String dateFormat, Boolean isParent, Boolean isOwner, List<CommentModel> children) {
        this.id = id;
        this.writer = writer;
        this.message = message;
        this.createAt = createAt;
        this.dateFormat = dateFormat;
        this.isParent = isParent;
        this.isOwner = isOwner;
        this.children = children;
    }

    public CommentModel(Comment comment, UserProfileResponseDTO writer) {
        this.id = comment.getId();
        this.writer = writer;
        this.message = comment.getMessage();
        this.createAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.dateFormat = LocalDateTimeUtil.getDateFormat(comment.getCreatedAt());
        this.isParent = comment.getParent() == null;
        this.deleted = comment.getDeleted();
        this.deletedAt = comment.getDeletedAt() != null ? comment.getDeletedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) : null;
    }

    public void changeIsOwner(Long loginUserId) {
        this.isOwner = writer.getId().equals(loginUserId);
    }

    public void addAllChildren(List<CommentModel> children) {
        this.children.addAll(children);
    }
}
