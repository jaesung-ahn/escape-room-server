package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.gathering.member.MemberModel;
import com.wiiee.server.api.application.user.UserModel;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.gathering.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
@Slf4j
public class GatheringSimpleModel {

    @Schema(description = "동행모집 아이디")
    Long id;
    @Schema(description = "해당 컨텐츠")
    ContentSimpleModel content;
    @Schema(description = "모임장")
    UserModel leader;
    @Schema(description = "모임 상태 명칭")
    String gatheringStatusName;
    @Schema(description = "모임 상태 코드")
    int gatheringStatusCode;
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
    @Schema(description = "동행 희망일 요일")
    String hopeDateDOW;
    @Schema(description = "카카오톡오픈챗 URL")
    String kakaoOpenChatUrl;
    @Schema(description = "동행모집 리더 유무")
    Boolean isOwner;
    @Schema(description = "등록일")
    String createdAt;

    @Schema(description = "북마크 수")
    Integer bookmarkCnt;

    @Schema(description = "코멘트 수")
    Integer commentCnt;

    @Schema(description = "동행 멤버")
    List<MemberModel> members;



    public static GatheringSimpleModel fromGatheringWithContentSimpleModel(Gathering gathering, ContentSimpleModel content, ImageService imageService) {
        GatheringInfo gatheringInfo = gathering.getGatheringInfo();

        GatheringStatus gatheringStatus = gatheringInfo.getGatheringStatus();
        // 동행 희망일 요일 day of week
        String hopeDateDOW = null;
        if (gatheringInfo.getRecruitType().equals(RecruitType.CONFIRM) && gatheringInfo.getHopeDate() != null &&
                gatheringStatus.equals(GatheringStatus.RECRUITING)) {
            // 희망일이 0 ~ 3일 전인 경우 마감 임박으로 상태 변경
            long countDays = LocalDate.now().until(gatheringInfo.getHopeDate(), ChronoUnit.DAYS);

            if (0 <= countDays && countDays < 4) {
                gatheringStatus = GatheringStatus.DEADLINE_IMMINENT;
            }
            else if (countDays < 0) { // 날짜 지난 경우 만료 상태로 변경
                gatheringStatus = GatheringStatus.RECRUIT_EXPIRED;
            }
        }

        if (gatheringInfo.getRecruitType().equals(RecruitType.CONFIRM) && gatheringInfo.getHopeDate() != null) {
            hopeDateDOW = getDayOfTheWeek(gatheringInfo);
        }

        return GatheringSimpleModel.builder()
                .id(gathering.getId())
                .content(content)
                .leader(UserModel.from(gathering.getLeader()))
                .gatheringStatusName(gatheringStatus.getName())
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
                .hopeDateDOW(hopeDateDOW)
                .kakaoOpenChatUrl(gatheringInfo.getKakaoOpenChatUrl())
                .createdAt(LocalDateTimeUtil.getDateFormat(gathering.getCreatedAt()))
                .bookmarkCnt(0)
                .commentCnt(0)
                .members(gathering.getGatheringMembers().stream().map(member -> {
                    String profileImageUrl = imageService.getImageById(member.getUser().getProfile().getProfileImageId()).getUrl();
                    return MemberModel.fromMember(member, profileImageUrl);
                }).collect(toList()))
                .build();
    }

    private static String getDayOfTheWeek(GatheringInfo gatheringInfo) {

        return LocalDateTimeUtil.getDayOfTheWeek(gatheringInfo.getHopeDate().atTime(12, 0));
    }

}
