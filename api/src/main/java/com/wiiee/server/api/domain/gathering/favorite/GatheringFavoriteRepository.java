package com.wiiee.server.api.domain.gathering.favorite;

import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteSimpleModel;
import com.wiiee.server.api.infrastructure.repository.gathering.favorite.GatheringFavoriteCustomRepository;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.favorite.GatheringFavorite;
import com.wiiee.server.common.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GatheringFavoriteRepository extends JpaRepository<GatheringFavorite, Long>, GatheringFavoriteCustomRepository {

    Integer countByGathering(Gathering gathering);

    Boolean existsByGatheringAndUser(Gathering gathering, User user);

    void deleteGatheringFavoriteByGatheringAndUser(Gathering gathering, User user);

    List<GatheringFavoriteSimpleModel> selectGatheringFavoriteSimpleModels(Long userId, Pageable pageable);

    Long countByUser(User user);

    Page<GatheringFavorite> findByUser(User user, Pageable pageable);
}
