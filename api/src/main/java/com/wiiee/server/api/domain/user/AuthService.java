package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.user.UserLoginRequestDTO;
import com.wiiee.server.api.application.user.UserPostRequestDTO;
import com.wiiee.server.api.application.user.UserSnsRequestDTO;
import com.wiiee.server.api.application.user.UserWithTokenModel;
import com.wiiee.server.api.domain.code.UserErrorCode;
import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import com.wiiee.server.api.infrastructure.repository.user.UserCustomRepository;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.api.application.user.UserSnSRequest;
import com.wiiee.server.common.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserWithTokenModel kakaoLogin(UserSnsRequestDTO requestDTO) {
        UserSnSRequest snsRequestDto = requestDTO.toKakaoUserSnsRequest();
        Optional<User> findUser = userCustomRepository.findByUserGetRequestDTO(snsRequestDto);
        boolean isSignUp = false;
        log.info("call kakaoLogin()1");

        // 해당 타입 유저가 없는 경우 회원가입
        if (findUser.isEmpty()) {
            // 같은 이메일 체크 후 있으면 아래 에러메시지 리턴
            userRepository.findByEmail(snsRequestDto.getEmail()).ifPresent(
                    u -> {
                        throw new ConflictException(UserErrorCode.ERROR_EMAIL_ALREADY_EXISTS);
                    }
            );

            findUser = Optional.of(userRepository.save(User.snsSignupOf(
                    snsRequestDto.getEmail(), snsRequestDto.getMemberType(), snsRequestDto.getUserOs()
            )));
            isSignUp = true;
        }

        final var user = findUser.orElseThrow(() -> new ResourceNotFoundException("로그인 실패되었습니다."));

        if (!isSignUp) {
            // 마지막 로그인 방문 업데이트
            user.updateLastLoginDate(LocalDate.now());
        }

        // 유저가 탈퇴 등 정상인 경우가 아닌 경우 에러 처리
        validateUserStatus(user);

        final var jwtModel = jwtTokenProvider.createToken(user.getEmail());
        user.updateRefreshToken(jwtModel.getRefreshToken());
        return UserWithTokenModel.fromUserAndToken(user, jwtModel, isSignUp);
    }

    @Transactional
    public UserWithTokenModel signup(UserPostRequestDTO dto) {
        User user = userRepository.save(dto.toUser(passwordEncoder));
        return UserWithTokenModel.fromUserAndToken(user, jwtTokenProvider.createToken(user.getEmail()), true);
    }

    @Transactional(readOnly = true)
    public UserWithTokenModel login(UserLoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .filter(it -> it.getPassword().matchesPassword(dto.getPassword(), passwordEncoder))
                .orElseThrow(NoSuchElementException::new);
        return UserWithTokenModel.fromUserAndToken(user, jwtTokenProvider.createToken(user.getEmail()));
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        // 푸시토큰 null 처리
        user.updatePushTokenAndUserOs(userId, null, null);
    }

    private void validateUserStatus(User user) {
        if (user.getProfile().getUserStatus().equals(UserStatus.WITHDRAWAL)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_WITHDRAWN);
        } else if (user.getProfile().getUserStatus().equals(UserStatus.BLOCK)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_BLOCKED);
        } else if (user.getProfile().getUserStatus().equals(UserStatus.DORMANT)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_DORMANT);
        }
    }
}
