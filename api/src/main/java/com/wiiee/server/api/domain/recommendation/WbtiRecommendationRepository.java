package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.common.domain.recommendation.WbtiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WbtiRecommendationRepository extends JpaRepository<WbtiRecommendation, Long> {

}
