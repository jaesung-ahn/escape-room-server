package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.recommendation.RecommendationModel;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserRecommendationService {

    @Transactional(readOnly = true)
    public List<RecommendationModel> getMyWbtiRecommends(User user, List<ContentSimpleModel> contentSimpleModelList) {
        List<RecommendationModel> list = new ArrayList<>();
        if (user.getProfile().getWbti() != null) {
            list.add(RecommendationModel.fromRecommendationAndContentSimpleModels(user.getProfile().getWbti().getName(),
                    contentSimpleModelList));
        }
        return list;
    }
}
