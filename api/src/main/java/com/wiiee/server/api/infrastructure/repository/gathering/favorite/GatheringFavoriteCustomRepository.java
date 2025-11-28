package com.wiiee.server.api.infrastructure.repository.gathering.favorite;

import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteSimpleModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatheringFavoriteCustomRepository {

    List<GatheringFavoriteSimpleModel> selectGatheringFavoriteSimpleModels(Long userId, Pageable pageable);

}
