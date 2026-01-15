package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.domain.code.UserErrorCode;
import com.wiiee.server.api.application.recommendation.RecommendationModel;
import com.wiiee.server.api.application.user.*;
import com.wiiee.server.api.domain.admin.AdminRepository;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.wbti.WbtiService;
import com.wiiee.server.api.infrastructure.external.kakao.KakaoApiService;
import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import com.wiiee.server.api.infrastructure.repository.gathering.GatheringCustomRepositoryImpl;
import com.wiiee.server.api.infrastructure.repository.user.UserCustomRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.user.*;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final WbtiService wbtiService;
    private final ImageService imageService;
    private final KakaoApiService kakaoApiService;
    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final GatheringCustomRepositoryImpl gatheringCustomRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Transactional
    public UserWithTokenModel kakaoLogin(UserSnsRequestDTO requestDTO) {

        UserSnSRequest snsRequestDto = requestDTO.toKakaoUserSnsRequest();
        Optional<User> findUser = userCustomRepository.findByUserGetRequestDTO(snsRequestDto);
        boolean isSignUp = false;
        log.info("call kakaoLogin()1");

//        KakaoUser kakaoUser = kakaoApiService.getKakaoUser(requestDTO.getAccessToken());
//        if (!kakaoUser.getEmail().equals(snsRequestDto.getEmail())) {
//            throw new RuntimeException("카카오 정보와 다른 이메일입니다.");
//        }
        // 해당 타입 유저가 없는 경우 회원가입
        if(findUser.isEmpty()) {
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
        if (user.getProfile().getUserStatus().equals(UserStatus.WITHDRAWAL)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_WITHDRAWN);
        }
        else if (user.getProfile().getUserStatus().equals(UserStatus.BLOCK)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_BLOCKED);
        }
        else if (user.getProfile().getUserStatus().equals(UserStatus.DORMANT)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_DORMANT);
        }

        final var jwtModel = jwtTokenProvider.createToken(user.getEmail());
        user.updateRefreshToken(jwtModel.getRefreshToken());
        return UserWithTokenModel.fromUserAndToken(user, jwtModel, isSignUp);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        final var findUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("로그인 실패"));

        findUser.updateUser(userId, request);
        request.getWbtiId()
                .ifPresent(wbtiId -> findUser.getProfile()
                        .changeWbti(wbtiService.findById(wbtiId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위비티아이 입니다."))));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public AdminUser findAdminById(long adminId) {
        return adminRepository.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 관리자"));
    }

    @Transactional
    public void updateUserPushInfo(Long id, UserPushInfoRequestDTO dto) {
        User user = findById(id);
        user.updatePushTokenAndUserOs(user.getId(), dto.getPushToken(), dto.getUserOSCode());
    }

    /**
     * 회원가입 시 나머지 정보 입력
     */
    @Transactional
    public UserSignupEtcResponseDTO updateUserSignupEtc(UserSignupEtcRequestDTO.UserSignupEtcRequest requestDTO) {
        Optional<User> findUser = userRepository.findById(requestDTO.getUserId());
        User user = findUser.orElseThrow(() -> new ResourceNotFoundException("찾을 수 없는 유저입니다."));
        user.updateUserSignupEtc(requestDTO.getNickname(), requestDTO.getUserGenderType(), requestDTO.getBirthDate(),
                requestDTO.getCity());

        Profile profile = user.getProfile();
        return new UserSignupEtcResponseDTO(user.getId(), profile.getNickname(), profile.getUserGenderType(),
                profile.getBirthDate(), profile.getCity());
    }

    /**
     * 설정에서 회원 정보 변경
     */
    @Transactional
    public void updateSettingUserInfo(Long userId, updateSettingUserInfoRequestDTO requestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("찾을 수 없는 유저입니다."));
        user.updateSettingUserInfo(requestDTO.getUserGenderType(), requestDTO.getBirthDate());
    }

    @Transactional
    public UserMypageResponseDTO getMyPage(Long userId) {
        User user = this.findById(userId);

        String profileUrl = null;
        Profile profile = user.getProfile();
        if (profile.getProfileImageId() != null && profile.getProfileImageId() != 0) {
            profileUrl = imageService.getImageById(profile.getProfileImageId()).getUrl();
        }
        int gatheringCnt = gatheringCustomRepository.findCountByUser(user);

        Wbti wbti = profile.getWbti();
        String zamfitTest = null;
        if (!Hibernate.isInitialized(wbti)) {
            Wbti unproxyWbti = (Wbti) Hibernate.unproxy(wbti);
            zamfitTest = unproxyWbti.getName();
        }

        return UserMypageResponseDTO.fromUserMypageResponseDTO(user, profileUrl, gatheringCnt, zamfitTest);
    }

    @Transactional(readOnly = true)
    public List<RecommendationModel> getMyWbtiRecommends(User user, List<ContentSimpleModel> contentSimpleModelList) {
        List<RecommendationModel> list = new ArrayList<>();
        if (user.getProfile().getWbti() != null) {
            list.add(RecommendationModel.fromRecommendationAndContentSimpleModels(user.getProfile().getWbti().getName(),
                    contentSimpleModelList));
        }
        return list;
    }

    @Transactional
    public UserWithTokenModel create(UserPostRequestDTO dto) {
        User user = userRepository.save(dto.toUser(passwordEncoder));
        return UserWithTokenModel.fromUserAndToken(user, jwtTokenProvider.createToken(user.getEmail()), true);
    }

    @Transactional(readOnly = true)
    public UserWithTokenModel login(UserLoginRequestDTO dto) {
        User user = findByEmail(dto.getEmail())
                .filter(it -> it.getPassword().matchesPassword(dto.getPassword(), passwordEncoder))
                .orElseThrow(NoSuchElementException::new);
        return UserWithTokenModel.fromUserAndToken(user, jwtTokenProvider.createToken(user.getEmail()));
    }

    // 유저 푸시 알림설정 변경
    @Transactional
    public void updateUserPushNoti(Long userId, UserPushNotiRequestDTO requestDTO) {
        User user = findById(userId);

        user.getProfile().updatePushStatusIfPresent(
                modelMapper.map(requestDTO, UserPushStatusDto.class)
        );

    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        User user = findById(userId);
        // 푸시토큰 null 처리
        user.updatePushTokenAndUserOs(userId, null, null);
    }

    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        return userRepository.existsByProfile_Nickname(nickname);
    }

}
