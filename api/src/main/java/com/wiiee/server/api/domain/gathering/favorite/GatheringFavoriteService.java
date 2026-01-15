package com.wiiee.server.api.domain.gathering.favorite;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteModel;
import com.wiiee.server.api.application.gathering.favorite.MultipleGatheringFavoriteModel;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.gathering.favorite.GatheringFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GatheringFavoriteService {

    private final GatheringFavoriteRepository gatheringFavoriteRepository;
    private final UserService userService;
    private final GatheringService gatheringService;

    public GatheringFavoriteModel getFavorite(Long gatheringId, Long userId) {
        final var user = userService.findById(userId);
        final var gathering = gatheringService.findById(gatheringId);

        return GatheringFavoriteModel.of(gatheringFavoriteRepository.countByGathering(gathering),
                gatheringFavoriteRepository.existsByGatheringAndUser(gathering, user));
    }

    @Transactional
    public GatheringFavoriteModel addFavorite(Long gatheringId, Long userId) {
        final var user = userService.findById(userId);
        final var gathering = gatheringService.findById(gatheringId);

        if (gatheringFavoriteRepository.existsByGatheringAndUser(gathering, user)) {
            throw new ConflictException(GatheringErrorCode.ERROR_GATHERING_ALREADY_FAVORITED);
        }

        gatheringFavoriteRepository.save(GatheringFavorite.of(gathering, user));
        return GatheringFavoriteModel.of(gatheringFavoriteRepository.countByGathering(gathering), true);
    }

    @Transactional
    public GatheringFavoriteModel deleteFavorite(Long gatheringId, Long userId) {
        final var user = userService.findById(userId);
        final var gathering = gatheringService.findById(gatheringId);

        gatheringFavoriteRepository.deleteGatheringFavoriteByGatheringAndUser(gathering, user);
        return GatheringFavoriteModel.of(gatheringFavoriteRepository.countByGathering(gathering), false);
    }

    @Transactional
    public void deleteFavorite(List<Long> gatheringIds, Long userId) {
        final var user = userService.findById(userId);
        for (Long gatheringId : gatheringIds) {
            final var gathering = gatheringService.findById(gatheringId);
            gatheringFavoriteRepository.deleteGatheringFavoriteByGatheringAndUser(gathering, user);
        }
    }

    public MultipleGatheringFavoriteModel getMyFavoritesWithGathering(Long userId, PageRequestDTO dto) {
        final var user = userService.findById(userId);
        final var gatheringFavorites = gatheringFavoriteRepository.selectGatheringFavoriteSimpleModels(userId, PageRequest.of(dto.getPage() - 1, dto.getSize()));
        final var totalCount = gatheringFavoriteRepository.countByUser(user);
        final var hasNext = gatheringFavoriteRepository.findByUser(user, PageRequest.of(dto.getPage(), dto.getSize())).hasNext();
        return MultipleGatheringFavoriteModel.fromGatheringFavorites(gatheringFavorites, totalCount, hasNext);
    }
}
