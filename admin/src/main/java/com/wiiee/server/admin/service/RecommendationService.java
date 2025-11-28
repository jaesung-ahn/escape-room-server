package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.RecommendationForm;
import com.wiiee.server.admin.repository.RecommendationRepository;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.recommendation.Recommendation;
import com.wiiee.server.common.domain.recommendation.RecommendationInfo;
import com.wiiee.server.common.domain.user.UserGenderType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private ContentService contentService;

    @Transactional(readOnly = true)
    public List<RecommendationForm> findAll() {
        List<Recommendation> recommendations = recommendationRepository.findAll();
        List<RecommendationForm> recommendationForms =
                recommendations.stream().map(recommendation -> RecommendationForm.fromRecommendationSimpleForm(recommendation))
                        .collect(Collectors.toList());

        return recommendationForms;
    }

    @Transactional(readOnly = true)
    public RecommendationForm findByIdForForm(Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId).get();

        return RecommendationForm.fromRecommendationSimpleForm(recommendation);
    }

    public void updateRDT(RecommendationForm recommendationForm) {
        Recommendation recommendation = recommendationRepository.findById(recommendationForm.getId()).get();

        RecommendationInfo recommendationInfo = getRecommendationInfo(recommendationForm);
        List<RecommendationForm.ContentForm> contentForms = recommendationForm.getContentForms();
        List<Content> contents = getContentList(contentForms);
        recommendation.updateRDT(recommendationInfo, contents);
        recommendationRepository.save(recommendation);
    }



    public void saveRDT(RecommendationForm recommendationForm) {
        RecommendationInfo recommendationInfo = getRecommendationInfo(recommendationForm);
        List<RecommendationForm.ContentForm> contentForms = recommendationForm.getContentForms();
        List<Content> contents = getContentList(contentForms);
//        recommendation.updateRDT(recommendationInfo, contents);
        recommendationRepository.save(new Recommendation(recommendationInfo, contents));
    }

    private List<Content> getContentList(List<RecommendationForm.ContentForm> contentForms) {
        List<Content> contents = contentForms.stream().map(contentForm ->
                contentService.findByIdContent(contentForm.getContentId())
        ).collect(Collectors.toList());
        return contents;
    }

    private RecommendationInfo getRecommendationInfo(RecommendationForm recommendationForm) {
        RecommendationInfo recommendationInfo = new RecommendationInfo(
                recommendationForm.getCategoryName(),
                null,
                City.valueOf(recommendationForm.getCityCode()),
                UserGenderType.valueOf(recommendationForm.getUserGenderTypeCode()),
                AgeGroup.valueOf(recommendationForm.getAgeGroupInfoCode())
        );
        return recommendationInfo;
    }
}
