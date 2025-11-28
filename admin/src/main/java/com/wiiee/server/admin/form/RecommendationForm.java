package com.wiiee.server.admin.form;

import com.wiiee.server.admin.util.DateUtil;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.recommendation.Recommendation;
import com.wiiee.server.common.domain.recommendation.RecommendationInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Setter
@Getter
@Slf4j
public class RecommendationForm {

    private Long id;

    private String categoryName;
    private int cityCode = 0;
    private int userGenderTypeCode = 0;
    private int ageGroupInfoCode = 0;
    private List<ContentForm> contentForms;
    private String createdAt;

    @Builder(access = PROTECTED)
    @Setter
    @Getter
    @ToString
    public static class ContentForm {

        private Long contentId;
        private String contentName;

        public ContentForm(Long contentId, String contentName) {
            this.contentId = contentId;
            this.contentName = contentName;
        }

        public static ContentForm fromContentSimpleForm(Content content) {
            return ContentForm.builder()
                    .contentId(content.getId())
                    .contentName(content.getContentBasicInfo().getName())
                    .build();
        }

    }

    public static RecommendationForm recommendationForm() {
        return RecommendationForm.builder().build();
    }

    public static RecommendationForm recommendationForm(Long id, String categoryName) {
        return RecommendationForm.builder()
                .id(id)
                .categoryName(categoryName)
                .build();
    }

    public static RecommendationForm fromRecommendationSimpleForm(Recommendation recommendation) {
        RecommendationInfo recommendationInfo = recommendation.getRecommendationInfo();
        RecommendationForm recommendationForm = RecommendationForm.builder()
                .id(recommendation.getId())
                .categoryName(recommendationInfo.getCategoryName())
                .cityCode(recommendationInfo.getCity() != null ? recommendationInfo.getCity().getCode() : -1)
                .userGenderTypeCode(recommendationInfo.getUserGenderType() != null ? recommendationInfo.getUserGenderType().getCode() : -1)
                .ageGroupInfoCode(recommendationInfo.getAgeGroup() != null ? recommendationInfo.getAgeGroup().getCode() : -1)
                .contentForms(recommendation.getContents().stream().map(content ->
                    RecommendationForm.ContentForm.fromContentSimpleForm(content)
                ).collect(Collectors.toList()))
                .createdAt(DateUtil.formatDateTime(recommendation.getCreatedAt()))
                .build();

        log.debug(String.valueOf("recommendationForm = " + recommendationForm));
        System.out.println("recommendationForm contentForms = " + recommendationForm.getContentForms());
        return recommendationForm;
    }
}
