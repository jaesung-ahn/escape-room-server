package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.common.domain.recommendation.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
