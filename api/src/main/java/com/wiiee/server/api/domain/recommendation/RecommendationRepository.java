package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.common.domain.recommendation.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    /**
     * Recommendation과 연관된 Contents를 한 번에 조회 (N+1 문제 해결)
     */
    @Query("SELECT DISTINCT r FROM Recommendation r LEFT JOIN FETCH r.contents")
    List<Recommendation> findAllWithContents();
}
