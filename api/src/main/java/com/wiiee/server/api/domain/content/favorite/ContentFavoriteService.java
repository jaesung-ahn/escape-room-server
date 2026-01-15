package com.wiiee.server.api.domain.content.favorite;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.content.favorite.ContentFavoriteModel;
import com.wiiee.server.api.application.content.favorite.ContentFavoriteSimpleModel;
import com.wiiee.server.api.application.content.favorite.MultipleContentFavoriteModel;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.domain.code.ContentErrorCode;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.favorite.ContentFavorite;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ContentFavoriteService {

    private final ContentFavoriteRepository contentFavoriteRepository;
    private final UserService userService;
    private final ContentService contentService;

    public ContentFavoriteModel getFavorite(Long contentId, Long userId) {
        final var user = userService.findById(userId);
        final var content = contentService.findById(contentId).orElseThrow();

        return ContentFavoriteModel.of(contentFavoriteRepository.countByContent(content),
                contentFavoriteRepository.existsByContentAndUser(content, user));
    }

    @Transactional
    public ContentFavoriteModel addFavorite(Long contentId, Long userId) {
        final var user = userService.findById(userId);
        final var content = contentService.findById(contentId).orElseThrow();

        if(contentFavoriteRepository.existsByContentAndUser(content, user)) {
            throw new ConflictException(ContentErrorCode.ERROR_CONTENT_ALREADY_FAVORITED);
        }

        contentFavoriteRepository.save(ContentFavorite.of(content, user));
        return ContentFavoriteModel.of(contentFavoriteRepository.countByContent(content), true);
    }

    @Transactional
    public ContentFavoriteModel deleteFavorite(Long contentId, Long userId) {
        final var user = userService.findById(userId);
        final var content = contentService.findById(contentId).orElseThrow();

        contentFavoriteRepository.deleteContentFavoriteByContentAndUser(content, user);
        return ContentFavoriteModel.of(contentFavoriteRepository.countByContent(content), false);
    }

    @Transactional
    public void deleteFavorite(List<Long> contentIds, Long userId) {
        final var user = userService.findById(userId);

        for (Long contentId : contentIds) {
            final var content = contentService.findById(contentId).orElseThrow();
            contentFavoriteRepository.deleteContentFavoriteByContentAndUser(content, user);
        }
    }

    public MultipleContentFavoriteModel getMyFavoritesWithContent(Long userId, PageRequestDTO dto) {
        final var user = userService.findById(userId);
        final var contentFavorites = contentFavoriteRepository.selectContentFavoriteSimpleModels(userId, PageRequest.of(dto.getPage() - 1, dto.getSize()));
        final var totalCount = contentFavoriteRepository.countByUser(user);
        final var hasNext = contentFavoriteRepository.findByUser(user, PageRequest.of(dto.getPage(), dto.getSize())).hasNext();

        return MultipleContentFavoriteModel.fromContentFavorites(contentFavorites, totalCount, hasNext);
    }

}
