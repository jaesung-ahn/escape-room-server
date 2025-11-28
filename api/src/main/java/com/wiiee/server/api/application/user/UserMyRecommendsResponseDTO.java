package com.wiiee.server.api.application.user;

import com.wiiee.server.api.application.recommendation.RecommendationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class UserMyRecommendsResponseDTO {

    @Schema(description = "잼핏 테스트 보유 여부")
    Boolean isWbti;

    @Schema(description = "추천 모델 리스트")
    List<RecommendationModel> recommendationList;

    public UserMyRecommendsResponseDTO(Boolean isWbti, List<RecommendationModel> recommendationList) {
        this.isWbti = isWbti;
        this.recommendationList = recommendationList;
    }

    public static UserMyRecommendsResponseDTO fromUserMyRecommendsResponseDTO(Boolean isWbti,
                                                                              List<RecommendationModel> recommendationModelList) {
        return new UserMyRecommendsResponseDTO(isWbti, recommendationModelList);
    }

//    public static RecommendationModel fromRecommendationAndContentSimpleModels(Recommendation recommendation, List<ContentSimpleModel> contents) {
//        return new RecommendationModel(recommendation.getRecommendationInfo().getCategoryName(),
//                contents);
//    }
}
