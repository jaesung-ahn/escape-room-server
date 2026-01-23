package com.wiiee.server.api.domain.gathering.comment;

import com.wiiee.server.common.domain.gathering.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByGatheringIdAndParentIsNullOrderByCreatedAtDesc(Long gathering);

    @Query("SELECT DISTINCT c FROM Comment c " +
           "LEFT JOIN FETCH c.children " +
           "LEFT JOIN FETCH c.writer " +
           "WHERE c.gatheringId = :gatheringId AND c.parent IS NULL " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findAllWithChildrenByGatheringId(@Param("gatheringId") Long gatheringId);

    void deleteByGatheringId(Long gatheringId);
}
