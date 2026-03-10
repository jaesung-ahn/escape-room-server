package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.user.UserMypageResponseDTO;
import com.wiiee.server.api.application.user.UserSignupEtcRequestDTO;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.wbti.WbtiService;
import com.wiiee.server.api.infrastructure.repository.gathering.GatheringCustomRepositoryImpl;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService 단위 테스트")
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WbtiService wbtiService;
    @Mock
    private ImageService imageService;
    @Mock
    private GatheringCustomRepositoryImpl gatheringCustomRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new FixtureUser("user@test.com", "테스터");
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("본인이 아닌 사용자의 프로필을 수정하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotSelf() {
            // given
            UserUpdateRequest request = new UserUpdateRequest("닉네임", null, null, null, null);

            // when & then (authUserId=2, targetUserId=1)
            assertThatThrownBy(() -> userProfileService.updateUser(2L, 1L, request))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("존재하지 않는 사용자를 수정하면 ResourceNotFoundException 발생")
        void throwsNotFoundWhenUserNotExists() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());
            UserUpdateRequest request = new UserUpdateRequest("닉네임", null, null, null, null);

            // when & then
            assertThatThrownBy(() -> userProfileService.updateUser(999L, 999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateUserSignupEtc")
    class UpdateUserSignupEtc {

        @Test
        @DisplayName("본인이 아닌 사용자의 추가정보를 수정하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotSelf() {
            // given
            UserSignupEtcRequestDTO.UserSignupEtcRequest request =
                    new UserSignupEtcRequestDTO.UserSignupEtcRequest(1L, "닉네임", null, null, null);

            // when & then (authUserId=2)
            assertThatThrownBy(() -> userProfileService.updateUserSignupEtc(2L, request))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("checkNickname")
    class CheckNickname {

        @Test
        @DisplayName("중복 닉네임 존재 여부를 올바르게 반환한다")
        void returnsTrueWhenDuplicate() {
            // given
            given(userRepository.existsByProfile_Nickname("중복닉네임")).willReturn(true);
            given(userRepository.existsByProfile_Nickname("새닉네임")).willReturn(false);

            // when & then
            assertThat(userProfileService.checkNickname("중복닉네임")).isTrue();
            assertThat(userProfileService.checkNickname("새닉네임")).isFalse();
        }
    }

    @Nested
    @DisplayName("getMyPage")
    class GetMyPage {

        @Test
        @DisplayName("마이페이지를 정상적으로 조회한다")
        void getMyPageSuccessfully() {
            // given
            ReflectionTestUtils.setField(user.getProfile(), "profileImageId", 10L);
            ReflectionTestUtils.setField(user.getProfile(), "wbti", null);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(imageService.getImageById(10L)).willReturn(new Image("profile-url"));
            given(gatheringCustomRepository.findCountByUser(user)).willReturn(3);

            // when
            UserMypageResponseDTO result = userProfileService.getMyPage(1L);

            // then
            assertThat(result).isNotNull();
        }
    }
}
