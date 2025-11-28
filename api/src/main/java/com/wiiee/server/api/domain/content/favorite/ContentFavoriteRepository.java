package com.wiiee.server.api.domain.content.favorite;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.infrastructure.repository.content.favorite.ContentFavoriteCustomRepository;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.favorite.ContentFavorite;
import com.wiiee.server.common.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentFavoriteRepository extends JpaRepository<ContentFavorite, Long>, ContentFavoriteCustomRepository {

    Integer countByContent(Content content);

    Boolean existsByContentAndUser(Content content, User user);

    void deleteContentFavoriteByContentAndUser(Content content, User user);

    List<ContentSimpleModel> selectContentFavoriteSimpleModels(Long userId, Pageable pageable);

    Long countByUser(User user);

    Page<ContentFavorite> findByUser(User user, Pageable pageable);
}
