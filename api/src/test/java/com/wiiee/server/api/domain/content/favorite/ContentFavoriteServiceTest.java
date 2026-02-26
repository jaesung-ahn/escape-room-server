package com.wiiee.server.api.domain.content.favorite;

import com.wiiee.server.api.application.content.favorite.ContentFavoriteModel;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.user.FixtureUser;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContentFavoriteService 단위 테스트")
class ContentFavoriteServiceTest {

    @Mock
    private ContentFavoriteRepository contentFavoriteRepository;
    @Mock
    private UserService userService;
    @Mock
    private ContentService contentService;

    @InjectMocks
    private ContentFavoriteService contentFavoriteService;

    private User user;
    private Content content;

    @BeforeEach
    void setUp() {
        user = new FixtureUser("user@test.com", "테스터");
        content = new Content(null, new ContentBasicInfo("놀거리"));
        ReflectionTestUtils.setField(content, "id", 1L);
    }

    @Test
    @DisplayName("이미 찜한 놀거리를 다시 찜하면 ConflictException 발생")
    void throwsConflictWhenAlreadyFavorited() {
        // given
        given(userService.findById(1L)).willReturn(user);
        given(contentService.findById(1L)).willReturn(Optional.of(content));
        given(contentFavoriteRepository.existsByContentAndUser(content, user)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> contentFavoriteService.addFavorite(1L, 1L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("찜하지 않은 놀거리를 정상적으로 찜한다")
    void addFavoriteSuccessfully() {
        // given
        given(userService.findById(1L)).willReturn(user);
        given(contentService.findById(1L)).willReturn(Optional.of(content));
        given(contentFavoriteRepository.existsByContentAndUser(content, user)).willReturn(false);
        given(contentFavoriteRepository.countByContent(content)).willReturn(1);

        // when
        ContentFavoriteModel result = contentFavoriteService.addFavorite(1L, 1L);

        // then
        assertThat(result.getIsFavorite()).isTrue();
        assertThat(result.getCount()).isEqualTo(1);
        verify(contentFavoriteRepository).save(any());
    }
}
