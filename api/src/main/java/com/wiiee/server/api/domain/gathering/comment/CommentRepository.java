package com.wiiee.server.api.domain.gathering.comment;

import com.wiiee.server.common.domain.gathering.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByGatheringIdAndParentIsNullOrderByCreatedAtDesc(Long gathering);

    void deleteByGatheringId(Long gatheringId);
}
