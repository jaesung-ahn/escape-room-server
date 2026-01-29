package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.BadRequestException;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.gathering.GatheringCancelReqDTO;
import com.wiiee.server.api.application.gathering.GatheringConfirmReqDTO;
import com.wiiee.server.api.application.gathering.GatheringRequestDetailResDTO;
import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import com.wiiee.server.common.domain.gathering.RecruitType;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatheringRequestService {

    private final GatheringRepository gatheringRepository;
    private final GatheringRequestRepository gatheringRequestRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final GatheringNotificationService gatheringNotificationService;

    /**
     * 동행모집 참가 신청
     */
    @Transactional
    public GatheringRequest applyGathering(long gatheringId, long userId, String requestReason) {
        final var user = userService.findById(userId);
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(NoSuchElementException::new);

        if (gathering.getGatheringInfo().getGatheringStatus().equals(GatheringStatus.RECRUIT_COMPLETED)) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_COMPLETED);
        } else if (gathering.getGatheringInfo().getGatheringStatus().equals(GatheringStatus.RECRUIT_EXPIRED)) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_EXPIRED);
        }

        int memberSize = gathering.getGatheringMembers().size();
        if (memberSize == gathering.getGatheringInfo().getMaxPeople()) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_MAX_MEMBER);
        }

        // 같은 동행모집의 같은 멤버가 중복 신청하는 경우
        if (gathering.getGatheringMembers().stream().anyMatch(gatheringMember -> gatheringMember.getUser().equals(user))) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_ALREADY_APPLIED);
        }

        // 승낙제에서 같은 사용자의 동행모집 신청서 존재하는데 신청하는 경우
        if (RecruitType.CONFIRM.equals(gathering.getGatheringInfo().getRecruitType())) {
            if (gatheringRequestRepository.findAllByGathering(gathering).stream()
                    .anyMatch(gatheringRequest -> gatheringRequest.getRequestUser().equals(user))) {
                throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_ALREADY_APPLIED);
            }
        }

        // 승낙제 동행모집 참가 신청
        if (RecruitType.CONFIRM.equals(gathering.getGatheringInfo().getRecruitType())) {
            GatheringRequest gatheringRequest = GatheringRequest.builder()
                    .requestUser(user)
                    .gathering(gathering)
                    .requestReason(requestReason)
                    .build();

            GatheringRequest savedGatheringRequest = gatheringRequestRepository.save(gatheringRequest);

            // 동행 신청 푸시 보내기
            gatheringNotificationService.sendGatheringRequestPush(gathering, savedGatheringRequest);

            return savedGatheringRequest;
        }
        // 선착순 동행모집 참가 신청
        else if (RecruitType.FIRST_COME.equals(gathering.getGatheringInfo().getRecruitType())) {
            gathering.addMember(user);
        }

        return null;
    }

    /**
     * 동행모집 참가서 상세 조회
     */
    @Transactional
    public GatheringRequestDetailResDTO getGatheringRequestDetail(Long gatheringRequestId, Long userId) {
        final var gatheringRequest = gatheringRequestRepository.findById(gatheringRequestId).orElseThrow();
        Image userImage = imageService.getImageById(gatheringRequest.getRequestUser().getProfile().getProfileImageId());

        final Gathering unproxyGathering = Hibernate.unproxy(gatheringRequest.getGathering(), Gathering.class);

        // 권한 검증: 호스트 또는 신청자만 조회 가능
        boolean isHost = unproxyGathering.getLeader().getId().equals(userId);
        boolean isApplicant = gatheringRequest.getRequestUser().getId().equals(userId);
        if (!isHost && !isApplicant) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_REQUEST_NOT_ACCESSIBLE);
        }

        // 동행모집 호스트이며,
        // 참가서 상태가 '주최자 확인 전' 상태인 경우만 '주최자 확인 됨' 으로 변경
        if (isHost && gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.UNVERIFIED)) {
            gatheringRequest.updateRequestStatus(GatheringRequestStatus.VERIFIED);
        }

        return GatheringRequestDetailResDTO.fromGatheringRequestDetail(gatheringRequest,
                UserProfileResponseDTO.from(gatheringRequest.getRequestUser(), userImage));
    }

    /**
     * 호스트가 동행모집 참가서 수락, 거절
     */
    @Transactional
    public GatheringRequest confirmGatheringRequest(GatheringConfirmReqDTO gatheringConfirmReqDTO, Long userId) {
        final var gatheringRequest = gatheringRequestRepository.findById(gatheringConfirmReqDTO.getGatheringRequestId()).orElseThrow();

        if (gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.UNVERIFIED) ||
                gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.VERIFIED)) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        if (gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.APPROVAL) ||
                gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.REJECT)) {
            throw new ConflictException(GatheringErrorCode.ERROR_GATHERING_REQUEST_INVALID_STATUS);
        }

        final User requestUser = gatheringRequest.getRequestUser();
        final User unproxyRequestUser = Hibernate.unproxy(requestUser, User.class);
        final Gathering gathering = gatheringRequest.getGathering();
        final Gathering unproxyGathering = Hibernate.unproxy(gathering, Gathering.class);

        if (!unproxyGathering.getLeader().getId().equals(userId)) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_NOT_HOST);
        }

        int memberSize = unproxyGathering.getGatheringMembers().size();
        if (memberSize == unproxyGathering.getGatheringInfo().getMaxPeople()) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_MAX_MEMBER);
        }

        if (unproxyGathering.getGatheringMembers().stream().anyMatch(gatheringMember ->
                gatheringMember.getUser().equals(unproxyRequestUser))) {
            throw new ConflictException(GatheringErrorCode.ERROR_GATHERING_MEMBER_ALREADY_EXISTS);
        }

        if (gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.APPROVAL)) {
            // 승낙제 인 경우 신청한 유저 동행모집 멤버에 추가
            unproxyGathering.addMember(unproxyRequestUser);
        }

        gatheringNotificationService.sendGatheringConfirmPush(unproxyGathering, gatheringRequest,
                gatheringConfirmReqDTO.getGatheringRequestStatus());

        // 동행모집 참가서 상태 변경
        gatheringRequest.updateRequestStatus(gatheringConfirmReqDTO.getGatheringRequestStatus());

        return gatheringRequest;
    }

    /**
     * 동행모집 참가서 취소
     */
    @Transactional
    public GatheringRequest cancelGatheringRequest(GatheringCancelReqDTO gatheringCancelReqDTO, long userId) {
        GatheringRequest gatheringRequest = gatheringRequestRepository.findById(gatheringCancelReqDTO.getGatheringRequestId())
                .orElseThrow(NoSuchElementException::new);
        User requestUser = gatheringRequest.getRequestUser();
        User unproxyOrgRequestUser = requestUser;
        if (!Hibernate.isInitialized(requestUser)) {
            unproxyOrgRequestUser = (User) Hibernate.unproxy(requestUser);
        }

        // 본인 동행모집 참가서인지 확인
        if (!unproxyOrgRequestUser.getId().equals(userId)) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_REQUEST_IS_NOT_YOUR_REQ);
        }

        // 동행모집 참가서 상태가 '호스트 확인 전', '호스트 확인 됨' 두 상태가 아닌 경우 예외처리
        if (!(gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.UNVERIFIED) ||
                gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.VERIFIED))) {
            throw new BadRequestException(GatheringErrorCode.ERROR_GATHERING_REQUEST_IS_NON_CANCELLABLE);
        }

        // 요청자 취소 상태로 변경
        gatheringRequest.updateRequestStatus(GatheringRequestStatus.CANCELED);

        return gatheringRequest;
    }

    /**
     * 승인된 참가서 조회 (내부용)
     */
    @Transactional(readOnly = true)
    public GatheringRequest findApprovedGatheringRequest(Gathering gathering, User user) {
        return gatheringRequestRepository.findApprovedGatheringRequest(gathering, user);
    }
}
