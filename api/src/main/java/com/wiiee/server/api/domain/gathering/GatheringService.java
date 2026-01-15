package com.wiiee.server.api.domain.gathering;

import com.google.common.net.HttpHeaders;
import com.wiiee.server.api.application.exception.BadRequestException;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.*;
import com.wiiee.server.api.application.gathering.member.WaitingMemberModel;
import com.wiiee.server.api.application.gathering.mgr.GatheringManager;
import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.gathering.comment.CommentService;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import com.wiiee.server.common.domain.gathering.RecruitType;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hibernate.Hibernate;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Value(value = "${push.api.url}")
    private String BASE_PUSH_URL;

    @Value(value = "${push.enabled:true}")
    private boolean pushEnabled;

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
                this.getWaitingMember(createdGathering), 0L, imageService
        );
    }

    /**
     * 동행모집 수정
     */
    @Transactional
    public void updateGathering(GatheringUpdateRequestDTO dto, Long userId) {

        GatheringManager.checkKakaoOpenUrl(dto.getKakaoOpenChatUrl());

        Gathering gathering = findById(dto.getGatheringId());
        if (!gathering.getLeader().getId().equals(userId)){
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

        Long imageId = gathering.getLeader().getProfile().getProfileImageId() != null ? gathering.getLeader().getProfile().getProfileImageId() : null;
        Image userImage = imageService.getImageById(imageId);
        long unverifiedGatherCnt = gatheringRequestRepository.countUnverifiedRequestByGathering(gathering);

        return GatheringModel.fromGatheringWithContentModel(
            userId, gathering, contentService.getContentModelByContent(gathering.getContent()), userImage,
                this.getWaitingMember(gathering), unverifiedGatherCnt, imageService
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
                PageRequest.of(
                        dto.getPage() - 1,
                        dto.getSize())
        );
        final var gatheringModels = gatherings.stream().map(gathering ->
                GatheringSimpleModel.fromGatheringWithContentSimpleModel(gathering, contentService.getContentSimpleModelByContent(gathering.getContent()), imageService))
                .collect(Collectors.toList());
        return MultipleGatheringModel.fromGatherings(gatheringModels, gatherings.getTotalElements(), gatherings.hasNext());
    }


    /**
     * 동행모집 참가 신청
     */
    @Transactional
    public GatheringRequest applyGathering(long gatheringId, long userId, String requestReason) {
        final var user = userService.findById(userId);
        Gathering gathering = findById(gatheringId);

        if (gathering.getGatheringInfo().getGatheringStatus().equals(GatheringStatus.RECRUIT_COMPLETED)) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_COMPLETED);
        }
        else if (gathering.getGatheringInfo().getGatheringStatus().equals(GatheringStatus.RECRUIT_EXPIRED)) {
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

            if (gatheringRequestRepository.findAllByGathering(gathering).stream().anyMatch(gatheringRequest -> gatheringRequest.getRequestUser().equals(user))) {
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

            // 동행 신청 푸시 보내기 임의 코드 작성
            sendGatheringRequestPush(gathering, savedGatheringRequest);

            return savedGatheringRequest;
        }
        // 선착순 동행모집 참가 신청
        else if (RecruitType.FIRST_COME.equals(gathering.getGatheringInfo().getRecruitType())) {

            gathering.addMember(user);
        }

        return null;
    }

    /**
     * 동행 신청 푸시 요청
     */
    private void sendGatheringRequestPush(Gathering gathering, GatheringRequest gatheringRequest) {
        log.info("call sendGatheringRequestPush()");

        if (!pushEnabled) {
            log.info("[MOCK] Push notification would be sent - GatheringId: {}, UserId: {}",
                    gathering.getId(), gatheringRequest.getRequestUser().getId());
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("gatheringId", gathering.getId());
            requestMap.put("gatheringMemberId", gatheringRequest.getId());
            requestMap.put("leaderId", gathering.getLeader().getId());
            requestMap.put("userId", gatheringRequest.getRequestUser().getId());

            String message = new JSONObject(requestMap).toJSONString();

            log.info("sendGatheringRequestPush request message = " + message);

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            String API_URL = BASE_PUSH_URL + "/sendGatheringRequestPush";

            Request requestObj = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(requestObj)
                    .execute();

            log.info("response.body():" + (response.body() != null ? response.body().string() : null));
        } catch (IOException e) {
                log.error("Failed to send push notification", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending push notification", e);
        }
    }

    @Transactional(readOnly = true)
    public List<WaitingMemberModel> getWaitingMember(Gathering gathering) {
        return gatheringRequestRepository.findAllByGathering(gathering).stream().map(
                gatheringRequest -> {
                    String url = imageService.getImageById(gatheringRequest.getRequestUser().getProfile().getProfileImageId()).getUrl();
                    return WaitingMemberModel.fromGatheringRequest(gatheringRequest, url);
                }
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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
     * @param gatheringConfirmReqDTO
     * @param userId
     * @return GatheringRequest
     */
    @Transactional
    public GatheringRequest confirmGatheringRequest(GatheringConfirmReqDTO gatheringConfirmReqDTO, Long userId) {
        final var gatheringRequest = gatheringRequestRepository.findById(gatheringConfirmReqDTO.getGatheringRequestId()).orElseThrow();

        if (gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.UNVERIFIED) ||
                gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.VERIFIED) ) {
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

        if (unproxyGathering.getLeader().getId() != userId) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_NOT_HOST);
        }

        int memberSize = unproxyGathering.getGatheringMembers().size();
        if (memberSize == unproxyGathering.getGatheringInfo().getMaxPeople()) {
            throw new ConflictException(GatheringErrorCode.ERROR_RECRUIT_MAX_MEMBER);
        }

        if (unproxyGathering.getGatheringMembers().stream().anyMatch(gatheringMember ->
                gatheringMember.getUser().equals(unproxyRequestUser)) ) {
            throw new ConflictException(GatheringErrorCode.ERROR_GATHERING_MEMBER_ALREADY_EXISTS);
        }

        if (gatheringConfirmReqDTO.getGatheringRequestStatus().equals(GatheringRequestStatus.APPROVAL)) {
            // 승낙제 인 경우 신청한 유저 동행모집 멤버에 추가
            unproxyGathering.addMember(unproxyRequestUser);
        }

        sendGatheringConfirmPush(unproxyGathering, gatheringRequest, gatheringConfirmReqDTO.getGatheringRequestStatus());

        // 동행모집 참가서 상태 변경
        gatheringRequest.updateRequestStatus(gatheringConfirmReqDTO.getGatheringRequestStatus());

        return gatheringRequest;
    }

    /**
     * 동행 수락,거절 푸시 요청
     */
    private void sendGatheringConfirmPush(Gathering gathering, GatheringRequest gatheringRequest, GatheringRequestStatus gatheringRequestStatus) {
        log.info("call sendGatheringConfirmPush()");
        try {
            OkHttpClient client = new OkHttpClient();

            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("gatheringId", gathering.getId());
            requestMap.put("userId", gatheringRequest.getRequestUser().getId());
            requestMap.put("confirmCode", gatheringRequestStatus.getCode());

            String message = new JSONObject(requestMap).toJSONString();

            log.info("request message = " + message);

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            String API_URL = BASE_PUSH_URL + "/sendGatheringConfirmPush";

            Request requestObj = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(requestObj)
                    .execute();

            log.info("response.body():" + (response.body() != null ? response.body().string() : null));
        } catch (IOException e) {
            log.error("Failed to send push notification", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending push notification", e);
        }
    }

    /**
     * 동행모집 참가서 취소
     * @param gatheringCancelReqDTO
     * @param userId
     * @return GatheringRequest
     */
    @Transactional
    public GatheringRequest cancelGatheringRequest(GatheringCancelReqDTO gatheringCancelReqDTO, long userId) {
        GatheringRequest gatheringRequest = gatheringRequestRepository.findById(gatheringCancelReqDTO.getGatheringRequestId()).orElseThrow(NoSuchElementException::new);
        User requestUser = gatheringRequest.getRequestUser();
        User unproxyOrgRequestUser = requestUser;
        if (!Hibernate.isInitialized(requestUser)) {
            unproxyOrgRequestUser = (User) Hibernate.unproxy(requestUser);
        }

        // 본인 동행모집 참가서인지 확인
        if (unproxyOrgRequestUser.getId() != userId) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_REQUEST_IS_NOT_YOUR_REQ);
        }
        // 동행모집 참가서 상태가 '호스트 확인 전', '호스트 확인 됨' 두 상태가 아닌 경우 예외처리
        if (!(gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.UNVERIFIED) ||
                gatheringRequest.getGatheringRequestStatus().equals(GatheringRequestStatus.VERIFIED))
        ) {
            throw new BadRequestException(GatheringErrorCode.ERROR_GATHERING_REQUEST_IS_NON_CANCELLABLE);
        }

        // 요청자 취소 상태로 변경
        gatheringRequest.updateRequestStatus(GatheringRequestStatus.CANCELED);

        return gatheringRequest;
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
                .filter(gathering -> gatheringStatusCheckList.contains(gathering.getGatheringStatusCode() ) )
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
        this.checkGatheringHost(userId, gathering);
        gathering.getGatheringInfo().updateGatheringStatus(GatheringStatus.RECRUIT_COMPLETED);
    }

    /**
     * 동행모집 '모집중'으로 변경
     */
    @Transactional
    public void recruitingGathering(Long gatheringId, long userId) {
        Gathering gathering = findById(gatheringId);
        this.checkGatheringHost(userId, gathering);
        gathering.getGatheringInfo().updateGatheringStatus(GatheringStatus.RECRUITING);
    }

    // 동행모집의 호스트인지 검사
    private void checkGatheringHost(long userId, Gathering gathering) {
        if (!gathering.getLeader().getId().equals(userId)) {
            throw new ForbiddenException(GatheringErrorCode.ERROR_GATHERING_IS_NOT_MINE);
        }
    }

    /**
     * 동행모집 참여된 상태에서 참여 취소
     */
    @Transactional
    public void cancelJoinGathering(Long gatheringId, long userId) {
        Gathering gathering = this.findById(gatheringId);
        User user = userService.findById(userId);

        // 참여로 된 멤버 있는지 검사
        GatheringMember gatheringMember = gatheringRepository.findGatheringMember(gathering, user);

        GatheringRequest gatheringRequest = gatheringRequestRepository.findApprovedGatheringRequest(gathering, user);
        // 요청자 참여 취소 상태로 변경
        gatheringRequest.updateRequestStatus(GatheringRequestStatus.CANCELED_JOIN);

        // 참여 멤버 삭제
        gathering.deleteMember(gatheringMember);
    }

    /**
     * 동행모집 삭제
     */
    @Transactional
    public void deleteGathering(Long gatheringId, Long userId) {
        Gathering gathering = this.findById(gatheringId);

        this.checkGatheringHost(userId, gathering);

        User user = userService.findById(userId);

        // 동행 멤버 모두 삭제
        gathering.deleteAllMember(gathering.getGatheringMembers());

        // 동행 댓글 삭제(실제 삭제)
        commentService.deleteAllByGatheringId(gathering.getId());

        // 동행 모집 삭제
        gathering.delete(userId);
    }
}
