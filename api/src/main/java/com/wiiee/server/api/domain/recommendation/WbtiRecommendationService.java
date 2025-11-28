package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.api.application.content.MultipleContentModel;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WbtiRecommendationService {

    private final WbtiRecommendationRepository wbtiRecommendationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Content> getWbtiRecommendationContents(Long wbtiId) {
        final var wbtiRecommendation = wbtiRecommendationRepository.findById(wbtiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 wbtiRecommendation을 찾을 수 없습니다."));

        return wbtiRecommendation.getContents();
    }

    @Transactional(readOnly = true)
    public List<Content> getWbtiRecommendationContentsByUserId(Long userId) {
        final var findUser = userService.findById(userId);

        if (findUser.getProfile().getWbti() != null) {
            final var wbtiRecommendation = wbtiRecommendationRepository.findById(findUser.getProfile().getWbti().getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 wbtiRecommendation을 찾을 수 없습니다."));
            return wbtiRecommendation.getContents();
        }

        return List.of();
    }

}
