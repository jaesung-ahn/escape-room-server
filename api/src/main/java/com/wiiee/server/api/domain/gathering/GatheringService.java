package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.BadRequestException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.*;
import com.wiiee.server.api.application.gathering.mgr.GatheringManager;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.gathering.comment.CommentService;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final GatheringRequestRepository gatheringRequestRepository;
    private final UserService userService;
    private final ContentService contentService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final GatheringMemberService gatheringMemberService;

    @Transactional
    public GatheringModel createNewGathering(GatheringPostRequestDTO dto, Long userId) {
        GatheringManager.checkKakaoOpenUrl(dto.getKakaoOpenChatUrl());

        final var user = userService.findById(userId);
        final var content = contentService.findById(dto.getContentId()).orElseThrow();
        final var createdGathering = gatheringRepository.save(user.addGathering(content, dto.toGatheringInfo()));

        createdGathering.addMember(user);
        Long imageId = user.getProfile().getProfileImageId() != null ? user.getProfile().getProfileImageId() : null;
        Image userImage = imageService.getImageById(imageId);
        return GatheringModel.fromGatheringWithContentModel(
                userId, createdGathering, contentService.getContentModelByContent(content), userImage,
                gatheringMemberService.getWaitingMember(createdGathering), 0L, imageService
        );
    }

    /**
     * 동행모집 수정
     */
    @Transactional
    public void updateGathering(GatheringUpdateRequestDTO dto, Long userId) {
        GatheringManager.checkKakaoOpenUrl(dto.getKakaoOpenChatUrl());

        Gathering gathering = findById(dto.getGatheringId());
        if (!gathering.getLeader().getId().equals(userId)) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_IS_NOT_MINE);
        }

        if (dto.getHopeDate() != null && LocalDate.now().isAfter(dto.getHopeDate())) {
            throw new BadRequestException(GatheringErrorCode.ERROR_HOPE_DATE_NOT_ALLOWED_BEFORE_NOW);
        }

        if (gathering.getGatheringMembers().size() > dto.getMaxPeople()) {
            throw new BadRequestException(GatheringErrorCode.ERROR_NOT_ALLOWED_UNDER_THE_CURRENT_MEMBER);
        }

        gathering.getGatheringInfo().updateGatheringInfo(dto.getTitle(), dto.getInformation(), dto.getStateCode(),
                dto.getCityCode(), dto.getRecruitTypeCode(), dto.getMaxPeople(), dto.getGenderTypeCode(),
                dto.getIsDateAgreement(), dto.getHopeDate(), dto.getKakaoOpenChatUrl());
    }

    /**
     * 동행모집 상세 조회
     */
    @Transactional(readOnly = true)
    public GatheringModel getGatheringDetail(Long id, Long userId) {
        final var gathering = gatheringRepository.findById(id).orElseThrow();

        if (gathering.getDeleted()) {
            throw new ResourceNotFoundException(GatheringErrorCode.ERROR_DELETED_GATHERING);
        }

        Long imageId = gathering.getLeader().getProfile().getProfileImageId() != null
                ? gathering.getLeader().getProfile().getProfileImageId() : null;
        Image userImage = imageService.getImageById(imageId);
        long unverifiedGatherCnt = gatheringRequestRepository.countUnverifiedRequestByGathering(gathering);

        return GatheringModel.fromGatheringWithContentModel(
                userId, gathering, contentService.getContentModelByContent(gathering.getContent()), userImage,
                gatheringMemberService.getWaitingMember(gathering), unverifiedGatherCnt, imageService
        );
    }

    @Transactional(readOnly = true)
    public Gathering findById(Long id) {
        return gatheringRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional(readOnly = true)
    public MultipleGatheringModel getGatherings(GatheringGetRequestDTO dto) {
        if (dto.getPage() == null) {
            dto.setPage(1);
        }
        if (dto.getSize() == null) {
            dto.setSize(10);
        }
        final var gatherings = gatheringRepository.findAllByGatheringGetRequestDTO(
                dto,
                PageRequest.of(dto.getPage() - 1, dto.getSize())
        );
        final var gatheringModels = gatherings.stream().map(gathering ->
                        GatheringSimpleModel.fromGatheringWithContentSimpleModel(
                                gathering, contentService.getContentSimpleModelByContent(gathering.getContent()), imageService))
                .collect(Collectors.toList());
        return MultipleGatheringModel.fromGatherings(gatheringModels, gatherings.getTotalElements(), gatherings.hasNext());
    }

    /**
     * 내 동행모집 목록
     */
    @Transactional
    public GatheringMyListResponseDTO getMyGatheringList(long userId) {
        User user = userService.findById(userId);
        List<Gathering> myGatheringList = gatheringRepository.findAllMyGathering(user);

        List<GatheringListModel> orgMyGatheringList = myGatheringList.stream()
                .map(gathering -> {
                    Long representativeImageId = gathering.getContent().getContentBasicInfo().getRepresentativeImageId();
                    Image contentImage = imageService.getImageById(representativeImageId);
                    return GatheringListModel.fromGatheringWithContentSimpleModel(gathering,
                            GatheringListContentModel.fromContentAndImage(gathering.getContent(), contentImage), imageService);
                }).toList();

        log.info("orgMyGatheringList:" + orgMyGatheringList);

        List<GatheringListModel> createdList = orgMyGatheringList.stream()
                .filter(gathering -> gathering.getLeaderId().equals(userId))
                .toList();

        List<Integer> gatheringStatusCheckList = new ArrayList<>();
        gatheringStatusCheckList.add(GatheringStatus.RECRUITING.getCode());
        gatheringStatusCheckList.add(GatheringStatus.DEADLINE_IMMINENT.getCode());

        List<GatheringListModel> ingList = orgMyGatheringList.stream()
                .filter(gathering -> gatheringStatusCheckList.contains(gathering.getGatheringStatusCode()))
                .toList();

        List<GatheringListModel> endList = orgMyGatheringList.stream()
                .filter(gathering -> gathering.getGatheringStatusCode().equals(GatheringStatus.RECRUIT_COMPLETED.getCode()))
                .toList();

        return GatheringMyListResponseDTO.builder()
                .createdList(createdList)
                .ingList(ingList)
                .endedList(endList)
                .build();
    }

    /**
     * 동행모집 완료로 변경
     */
    @Transactional
    public void completedGathering(Long gatheringId, long userId) {
        Gathering gathering = findById(gatheringId);
        checkGatheringHost(userId, gathering);
        gathering.getGatheringInfo().updateGatheringStatus(GatheringStatus.RECRUIT_COMPLETED);
    }

    /**
     * 동행모집 '모집중'으로 변경
     */
    @Transactional
    public void recruitingGathering(Long gatheringId, long userId) {
        Gathering gathering = findById(gatheringId);
        checkGatheringHost(userId, gathering);
        gathering.getGatheringInfo().updateGatheringStatus(GatheringStatus.RECRUITING);
    }

    private void checkGatheringHost(long userId, Gathering gathering) {
        if (!gathering.getLeader().getId().equals(userId)) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_IS_NOT_MINE);
        }
    }

    /**
     * 동행모집 삭제
     */
    @Transactional
    public void deleteGathering(Long gatheringId, Long userId) {
        Gathering gathering = findById(gatheringId);
        checkGatheringHost(userId, gathering);

        // 동행 멤버 모두 삭제
        gatheringMemberService.removeAllMembers(gathering);

        // 동행 댓글 삭제(실제 삭제)
        commentService.deleteAllByGatheringId(gathering.getId());

        // 동행 모집 삭제
        gathering.delete(userId);
    }
}
