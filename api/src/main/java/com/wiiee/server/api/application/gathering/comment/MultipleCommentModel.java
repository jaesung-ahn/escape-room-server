package com.wiiee.server.api.application.gathering.comment;

import lombok.Getter;

import java.util.List;

@Getter
public class MultipleCommentModel {

    private List<CommentModel> comments;
    private Integer count;

    protected MultipleCommentModel() {
    }

    public MultipleCommentModel(List<CommentModel> comments, Integer count) {
        this.comments = comments;
        this.count = count;
    }

    public static MultipleCommentModel of(List<CommentModel> comments) {
        return new MultipleCommentModel(comments, comments.size());
    }
}
