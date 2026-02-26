package com.wiiee.server.api.domain.gathering.favorite;

import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteModel;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.gathering.*;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatheringFavoriteService 단위 테스트")
class GatheringFavoriteServiceTest {

    @Mock
    private GatheringFavoriteRepository gatheringFavoriteRepository;
    @Mock
    private UserService userService;
    @Mock
    private GatheringService gatheringService;

    @InjectMocks
    private GatheringFavoriteService gatheringFavoriteService;

    private User user;
    private Gathering gathering;

    @BeforeEach
    void setUp() {
        user = new FixtureUser("user@test.com", "테스터");

        User host = new FixtureUser("host@test.com", "호스트");
        ReflectionTestUtils.setField(host, "id", 2L);
        Content content = new Content(null, new ContentBasicInfo("놀거리"));
        GatheringInfo info = new GatheringInfo("동행", "설명", State.SEOUL, City.GANGNAMGU,
                RecruitType.FIRST_COME, 4, GenderType.IRRELEVANT, true,
                LocalDate.now().plusDays(7), "", GatheringStatus.RECRUITING);
        gathering = new Gathering(content, host, info);
        ReflectionTestUtils.setField(gathering, "id", 10L);
    }

    @Test
    @DisplayName("이미 찜한 동행모집을 다시 찜하면 ConflictException 발생")
    void throwsConflictWhenAlreadyFavorited() {
        // given
        given(userService.findById(1L)).willReturn(user);
        given(gatheringService.findById(10L)).willReturn(gathering);
        given(gatheringFavoriteRepository.existsByGatheringAndUser(gathering, user)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> gatheringFavoriteService.addFavorite(10L, 1L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("찜하지 않은 동행모집을 정상적으로 찜한다")
    void addFavoriteSuccessfully() {
        // given
        given(userService.findById(1L)).willReturn(user);
        given(gatheringService.findById(10L)).willReturn(gathering);
        given(gatheringFavoriteRepository.existsByGatheringAndUser(gathering, user)).willReturn(false);
        given(gatheringFavoriteRepository.countByGathering(gathering)).willReturn(1);

        // when
        GatheringFavoriteModel result = gatheringFavoriteService.addFavorite(10L, 1L);

        // then
        assertThat(result.getIsFavorite()).isTrue();
        assertThat(result.getCount()).isEqualTo(1);
        verify(gatheringFavoriteRepository).save(any());
    }
}
