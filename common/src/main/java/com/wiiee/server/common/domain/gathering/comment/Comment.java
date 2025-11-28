package com.wiiee.server.common.domain.gathering.comment;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    private static final String DELETE_MESSAGE = "삭제된 댓글입니다.";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private Long gatheringId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User writer;

    @Column(length = 300)
    private String message;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id",
            referencedColumnName = "comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    public Comment(Long gatheringId, User writer, String message) {
        this.gatheringId = gatheringId;
        this.writer = writer;
        this.message = message;
    }

    public Comment(Long gatheringId, User writer, String message, Comment parent) {
        this.gatheringId = gatheringId;
        this.writer = writer;
        this.message = message;
        this.parent = parent;
    }

    public void changeMessage(Long userId, String message) {
        if (!writer.getId().equals(userId)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
        this.message = message;
    }

    public void delete(Long userId) {
        if (!writer.getId().equals(userId)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void addChildren(Comment childComment) {
        this.children.add(childComment);
    }
}
