package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.api.application.recommendation.RecommendationModel;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.common.domain.recommendation.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ContentService contentService;

    @Transactional(readOnly = true)
    public RecommendationModel getRecommendationModel() {
        final var recommendation = recommendationRepository.findById(1L).orElseThrow();
        return RecommendationModel.fromRecommendationAndContentSimpleModels(recommendation.getRecommendationInfo().getCategoryName(),
                contentService.getContentSimpleModelsByContents(recommendation.getContents()));
    }

    @Transactional(readOnly = true)
    public List<RecommendationModel> getRecommendations() {
        List<Recommendation> recommendations = recommendationRepository.findAll();
        return recommendations.stream().map(recommendation ->
                    RecommendationModel.fromRecommendationAndContentSimpleModels(
                            recommendation.getRecommendationInfo().getCategoryName(),
                            contentService.getContentSimpleModelsByContents(recommendation.getContents()))
                    )
                    .collect(Collectors.toList());
    }

}
