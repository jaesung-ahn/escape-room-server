package com.wiiee.server.api.infrastructure.repository.content.favorite;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentFavoriteCustomRepository {

    List<ContentSimpleModel> selectContentFavoriteSimpleModels(Long userId, Pageable pageable);

}
