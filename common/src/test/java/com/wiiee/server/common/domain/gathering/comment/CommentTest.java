package com.wiiee.server.common.domain.gathering.comment;

import com.wiiee.server.common.domain.user.FixtureUser;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    private User user = new FixtureUser("user@gmail.com", "user");

    @Test
    void 댓글_생성_테스트() {
        // given
        String message = "댓글 메세지";

        // when
        Comment comment = new Comment(1L, user, message);

        // then
        assertThat(comment.getMessage()).isEqualTo(message);
    }

    @Test
    void 대댓글_생성_테스트() {
        // given
        Comment parent = new Comment(1L, user, "원 댓글");

        // when
        Comment comment = new Comment(1L, parent.getWriter(), "대댓글", parent);

        // then
        assertThat(comment.getParent()).isEqualTo(parent);
    }
}