package com.wiiee.server.api.domain.user;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.user.*;
import com.wiiee.server.api.domain.code.UserErrorCode;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.wbti.WbtiService;
import com.wiiee.server.api.infrastructure.repository.gathering.GatheringCustomRepositoryImpl;
import com.wiiee.server.common.domain.user.Profile;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserPushStatusDto;
import com.wiiee.server.common.domain.user.UserUpdateRequest;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final WbtiService wbtiService;
    private final ImageService imageService;
    private final GatheringCustomRepositoryImpl gatheringCustomRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void updateUser(Long authUserId, Long targetUserId, UserUpdateRequest request) {
        if (!authUserId.equals(targetUserId)) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_UPDATE_PERMISSION_DENIED);
        }

        final var findUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        findUser.updateUser(targetUserId, request);
        request.getWbtiId()
                .ifPresent(wbtiId -> findUser.getProfile()
                        .changeWbti(wbtiService.findById(wbtiId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위비티아이 입니다."))));
    }

    @Transactional
    public UserSignupEtcResponseDTO updateUserSignupEtc(Long authUserId, UserSignupEtcRequestDTO.UserSignupEtcRequest requestDTO) {
        if (!authUserId.equals(requestDTO.getUserId())) {
            throw new ForbiddenException(UserErrorCode.ERROR_USER_UPDATE_PERMISSION_DENIED);
        }

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("찾을 수 없는 유저입니다."));
        user.updateUserSignupEtc(requestDTO.getNickname(), requestDTO.getUserGenderType(), requestDTO.getBirthDate(),
                requestDTO.getCity());

        Profile profile = user.getProfile();
        return new UserSignupEtcResponseDTO(user.getId(), profile.getNickname(), profile.getUserGenderType(),
                profile.getBirthDate(), profile.getCity());
    }

    @Transactional
    public void updateSettingUserInfo(Long userId, updateSettingUserInfoRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("찾을 수 없는 유저입니다."));
        user.updateSettingUserInfo(requestDTO.getUserGenderType(), requestDTO.getBirthDate());
    }

    @Transactional(readOnly = true)
    public UserMypageResponseDTO getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

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

    @Transactional
    public void updateUserPushInfo(Long id, UserPushInfoRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        user.updatePushTokenAndUserOs(user.getId(), dto.getPushToken(), dto.getUserOSCode());
    }

    @Transactional
    public void updateUserPushNoti(Long userId, UserPushNotiRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        user.getProfile().updatePushStatusIfPresent(
                modelMapper.map(requestDTO, UserPushStatusDto.class)
        );
    }

    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        return userRepository.existsByProfile_Nickname(nickname);
    }
}
