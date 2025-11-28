package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.content.ContentModel;
import com.wiiee.server.api.application.gathering.member.MemberModel;
import com.wiiee.server.api.application.gathering.member.WaitingMemberModel;
import com.wiiee.server.api.application.gathering.mgr.GatheringManager;
import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class GatheringModel {

    @Schema(description = "동행모집 아이디")
    Long id;
    @Schema(description = "해당 컨텐츠")
    ContentModel content;
    @Schema(description = "모임장")
    UserProfileResponseDTO leader;
    @Schema(description = "동행 모집 상태(모집중: 0, 모집 완료: 1, 모집 취소: 2, 모집 만료: 3, 마감 임박: 4)" )
    Integer gatheringStatusCode;
    @Schema(description = "동행 모집 타이틀")
    String title;
    @Schema(description = "소개글")
    String information;
    @Schema(description = "시도(큰 지역 단위)")
    String state;
    @Schema(description = "시군구(작은 지역 단위)")
    String city;
    @Schema(description = "모집 방식")
    String recruitType;
    @Schema(description = "최대 인원")
    Integer maxPeople;
    @Schema(description = "현재 인원")
    Integer currentPeople;
    @Schema(description = "연령대")
    List<String> ageGroups;
    @Schema(description = "성별 타입")
    String genderType;
    @Schema(description = "일시 협의 여부")
    Boolean isDateAgreement;
    @Schema(description = "동행 희망일")
    LocalDate hopeDate;
    @Schema(description = "카카오톡오픈챗 URL")
    String kakaoOpenChatUrl;
    @Schema(description = "동행모집 리더 유무")
    Boolean isOwner;
    @Schema(description = "동행모집 멤버 유무")
    Boolean isMember;

    @Schema(description = "동행 승락대기 멤버")
    List<WaitingMemberModel> waitingMembers;

    @Schema(description = "동행 멤버")
    List<MemberModel> members;

    @Schema(description = "동행 신청 알림 숫자")
    Long unverifiedGatherCnt;

    @Schema(description = "호스트가 아닌 경우 신청했던 참가서 아이디")
    Long gatheringRequestId;

    public static GatheringModel fromGatheringWithContentModel(Long userId, Gathering gathering, ContentModel content,
                                                               Image userImage, List<WaitingMemberModel> waitingMembers,
                                                               Long unverifiedGatherCnt) {
        GatheringInfo gatheringInfo = gathering.getGatheringInfo();
        List<MemberModel> members = gathering.getGatheringMembers().stream().map(MemberModel::fromMember).collect(toList());
        members.sort(Comparator.comparing(MemberModel::getIsOwner).reversed());

        Optional<WaitingMemberModel> waitingMember = waitingMembers.stream().filter(waitingMemberModel -> waitingMemberModel.getUserId().equals(userId)).findFirst();
        Long gatheringRequestId = null;
        if (waitingMember.isPresent()) {
            gatheringRequestId = waitingMember.get().getGatheringReqeustId();
        }
        int currentMemberSize = gathering.getGatheringMembers().size();
        GatheringStatus gatheringStatus = GatheringManager.getGatheringStatusInfo(gatheringInfo.getGatheringStatus(), gatheringInfo, currentMemberSize);

        return GatheringModel.builder()
                .id(gathering.getId())
                .content(content)
                .leader(UserProfileResponseDTO.from(gathering.getLeader(), userImage))
                .gatheringStatusCode(gatheringStatus.getCode())
                .title(gatheringInfo.getTitle())
                .information(gatheringInfo.getInformation())
                .state(gatheringInfo.getState().getName())
                .city(gatheringInfo.getCity().getName())
                .recruitType(gatheringInfo.getRecruitType().getName())
                .maxPeople(gatheringInfo.getMaxPeople())
                .currentPeople(gathering.getGatheringMembers().size())
                .ageGroups(gatheringInfo.getAgeGroupCodes().stream().map(code -> AgeGroup.valueOf(code).getName()).collect(toList()))
                .genderType(gatheringInfo.getGenderType().getName())
                .isDateAgreement(gatheringInfo.getIsDateAgreement())
                .hopeDate(gatheringInfo.getHopeDate())
                .kakaoOpenChatUrl(gatheringInfo.getKakaoOpenChatUrl())
                .isOwner(gathering.getLeader().getId().equals(userId))
                .isMember(gathering.isContainUser(userId))
                .waitingMembers(waitingMembers)
                .members(members)
                .unverifiedGatherCnt(unverifiedGatherCnt)
                .gatheringRequestId(gatheringRequestId)
                .build();
    }

}
