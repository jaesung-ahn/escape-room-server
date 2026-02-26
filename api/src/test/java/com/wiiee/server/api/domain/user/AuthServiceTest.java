package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.user.UserSnsRequestDTO;
import com.wiiee.server.api.application.user.UserSnSRequest;
import com.wiiee.server.api.application.user.UserWithTokenModel;
import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import com.wiiee.server.api.infrastructure.repository.user.UserCustomRepository;
import com.wiiee.server.common.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCustomRepository userCustomRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserSnsRequestDTO snsRequestDTO;

    @BeforeEach
    void setUp() {
        snsRequestDTO = new UserSnsRequestDTO();
        ReflectionTestUtils.setField(snsRequestDTO, "email", "new@test.com");
        ReflectionTestUtils.setField(snsRequestDTO, "accessToken", "kakao-token");
        ReflectionTestUtils.setField(snsRequestDTO, "userOs", UserOS.IOS);
    }

    private User createNormalUser(String email) {
        User user = User.snsSignupOf(email, MemberType.KAKAO, UserOS.IOS);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    @Nested
    @DisplayName("kakaoLogin")
    class KakaoLogin {

        @Test
        @DisplayName("신규 사용자는 회원가입 후 토큰이 반환된다")
        void newUserSignupAndReturnToken() {
            // given
            User newUser = createNormalUser("new@test.com");
            given(userCustomRepository.findByUserGetRequestDTO(any(UserSnSRequest.class))).willReturn(Optional.empty());
            given(userRepository.findByEmail("new@test.com")).willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(newUser);

            var jwtModel = mock(com.wiiee.server.api.application.security.JwtModel.class);
            given(jwtModel.getRefreshToken()).willReturn("refresh-token");
            given(jwtTokenProvider.createToken(anyString())).willReturn(jwtModel);

            // when
            UserWithTokenModel result = authService.kakaoLogin(snsRequestDTO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getIsSignUp()).isTrue();
        }

        @Test
        @DisplayName("이미 같은 이메일이 존재하면 ConflictException 발생")
        void throwsConflictWhenEmailExists() {
            // given
            User existingUser = createNormalUser("new@test.com");
            given(userCustomRepository.findByUserGetRequestDTO(any(UserSnSRequest.class))).willReturn(Optional.empty());
            given(userRepository.findByEmail("new@test.com")).willReturn(Optional.of(existingUser));

            // when & then
            assertThatThrownBy(() -> authService.kakaoLogin(snsRequestDTO))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("탈퇴 사용자가 로그인하면 ForbiddenException 발생")
        void throwsForbiddenWhenWithdrawn() {
            // given
            User withdrawnUser = createNormalUser("new@test.com");
            ReflectionTestUtils.setField(withdrawnUser.getProfile(), "userStatus", UserStatus.WITHDRAWAL);

            given(userCustomRepository.findByUserGetRequestDTO(any(UserSnSRequest.class)))
                    .willReturn(Optional.of(withdrawnUser));

            // when & then
            assertThatThrownBy(() -> authService.kakaoLogin(snsRequestDTO))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("차단된 사용자가 로그인하면 ForbiddenException 발생")
        void throwsForbiddenWhenBlocked() {
            // given
            User blockedUser = createNormalUser("new@test.com");
            ReflectionTestUtils.setField(blockedUser.getProfile(), "userStatus", UserStatus.BLOCK);

            given(userCustomRepository.findByUserGetRequestDTO(any(UserSnSRequest.class)))
                    .willReturn(Optional.of(blockedUser));

            // when & then
            assertThatThrownBy(() -> authService.kakaoLogin(snsRequestDTO))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("존재하지 않는 사용자로 로그아웃하면 ResourceNotFoundException 발생")
        void throwsNotFoundWhenUserNotExists() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.logout(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private static <T> T mock(Class<T> classToMock) {
        return org.mockito.Mockito.mock(classToMock);
    }
}
