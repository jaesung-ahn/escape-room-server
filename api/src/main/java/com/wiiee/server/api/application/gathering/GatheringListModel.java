package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.gathering.member.MemberModel;
import com.wiiee.server.api.application.gathering.mgr.GatheringManager;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import com.wiiee.server.common.domain.gathering.RecruitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
@Slf4j
public class GatheringListModel {

    @Schema(description = "동행모집 아이디")
    Long id;
    @Schema(description = "해당 컨텐츠")
    GatheringListContentModel content;

    @Schema(description = "모임 상태 명칭")
    String gatheringStatusName;
    @Schema(description = "모임 상태 코드")
    Integer gatheringStatusCode;
    @Schema(description = "동행 모집 타이틀")
    String title;

    @Schema(description = "모집 방식")
    String recruitType;
    @Schema(description = "최대 인원")
    Integer maxPeople;
    @Schema(description = "현재 인원")
    Integer currentPeople;

    @Schema(description = "일시 협의 여부")
    Boolean isDateAgreement;
    @Schema(description = "동행 희망일")
    LocalDate hopeDate;
    @Schema(description = "동행 희망일 요일")
    String hopeDateDOW;

    @Schema(description = "등록일")
    String createdAt;

    @Schema(description = "북마크 수")
    Integer bookmarkCnt;

    @Schema(description = "코멘트 수")
    Integer commentCnt;

    @Schema(description = "호스트 아이디")
    Long leaderId;

    @Schema(description = "동행 멤버")
    List<MemberModel> members;

    public static GatheringListModel fromGatheringWithContentSimpleModel(Gathering gathering, GatheringListContentModel content) {
        GatheringInfo gatheringInfo = gathering.getGatheringInfo();

        Integer maxPeople = gatheringInfo.getMaxPeople();
        int currentMemberSize = gathering.getGatheringMembers().size();
        GatheringStatus gatheringStatus = GatheringManager.getGatheringStatusInfo(gatheringInfo.getGatheringStatus(), gatheringInfo, currentMemberSize);

        // 동행 희망일 요일 day of week
        String hopeDateDOW = null;
        if (gatheringInfo.getRecruitType().equals(RecruitType.CONFIRM) && gatheringInfo.getHopeDate() != null) {
            hopeDateDOW = getDayOfTheWeek(gatheringInfo);
        }

        return GatheringListModel.builder()
                .id(gathering.getId())
                .content(content)
                .gatheringStatusName(gatheringStatus.getName())
                .gatheringStatusCode(gatheringStatus.getCode())
                .title(gatheringInfo.getTitle())
                .recruitType(gatheringInfo.getRecruitType().getName())
                .maxPeople(maxPeople)
                .currentPeople(gathering.getGatheringMembers().size())
                .isDateAgreement(gatheringInfo.getIsDateAgreement())
                .hopeDate(gatheringInfo.getHopeDate())
                .hopeDateDOW(hopeDateDOW)
                .createdAt(LocalDateTimeUtil.getDateFormat(gathering.getCreatedAt()))
                .bookmarkCnt(0)
                .commentCnt(0)
                .leaderId(gathering.getLeader().getId())
                .members(
                        gathering.getGatheringMembers().stream().map(MemberModel::fromMember)
                                .sorted(Comparator.comparing(MemberModel::getIsOwner).reversed())
                                .collect(toList())
                )
                .build();
    }

    private static String getDayOfTheWeek(GatheringInfo gatheringInfo) {

        return LocalDateTimeUtil.getDayOfTheWeek(gatheringInfo.getHopeDate().atTime(12, 0));
    }

}
