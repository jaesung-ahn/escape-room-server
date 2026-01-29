package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.member.WaitingMemberModel;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatheringMemberService {

    private final GatheringRepository gatheringRepository;
    private final GatheringRequestRepository gatheringRequestRepository;
    private final UserService userService;
    private final ImageService imageService;

    /**
     * 대기 멤버 조회
     */
    @Transactional(readOnly = true)
    public List<WaitingMemberModel> getWaitingMember(Gathering gathering) {
        List<GatheringRequest> requests = gatheringRequestRepository.findAllByGathering(gathering);

        // 프로필 이미지 ID 수집
        List<Long> imageIds = requests.stream()
                .map(req -> req.getRequestUser().getProfile().getProfileImageId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 배치로 이미지 조회 및 Map 변환
        HashMap<Long, Image> imageMap = imageService.findByIdsIn(imageIds).stream()
                .collect(Collectors.toMap(Image::getId, img -> img, (a, b) -> a, HashMap::new));

        // WaitingMemberModel 생성
        return requests.stream()
                .map(gatheringRequest -> {
                    Long profileImageId = gatheringRequest.getRequestUser().getProfile().getProfileImageId();
                    String url = profileImageId != null && imageMap.containsKey(profileImageId)
                            ? imageMap.get(profileImageId).getUrl()
                            : null;
                    return WaitingMemberModel.fromGatheringRequest(gatheringRequest, url);
                })
                .collect(Collectors.toList());
    }

    /**
     * 동행모집 참여된 상태에서 참여 취소
     */
    @Transactional
    public void cancelJoinGathering(Long gatheringId, long userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new ResourceNotFoundException(GatheringErrorCode.ERROR_DELETED_GATHERING));
        User user = userService.findById(userId);

        // 참여로 된 멤버 있는지 검사
        GatheringMember gatheringMember = gatheringRepository.findGatheringMember(gathering, user);
        if (gatheringMember == null) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_MEMBER_NOT_FOUND);
        }

        GatheringRequest gatheringRequest = gatheringRequestRepository.findApprovedGatheringRequest(gathering, user);
        if (gatheringRequest == null) {
            throw new ResourceNotFoundException(GatheringErrorCode.ERROR_APPROVED_REQUEST_NOT_FOUND);
        }

        // 요청자 참여 취소 상태로 변경
        gatheringRequest.updateRequestStatus(GatheringRequestStatus.CANCELED_JOIN);

        // 참여 멤버 삭제
        gathering.deleteMember(gatheringMember);
    }

    /**
     * 멤버 추가
     */
    public void addMember(Gathering gathering, User user) {
        gathering.addMember(user);
    }

    /**
     * 멤버 삭제
     */
    public void removeMember(Gathering gathering, GatheringMember member) {
        gathering.deleteMember(member);
    }

    /**
     * 멤버 전체 삭제
     */
    public void removeAllMembers(Gathering gathering) {
        gathering.deleteAllMember(gathering.getGatheringMembers());
    }
}
