package com.wiiee.server.api.application.gathering;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import com.wiiee.server.common.domain.gathering.GenderType;
import com.wiiee.server.common.domain.gathering.RecruitType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GatheringPostRequestDTO {

    Long contentId;

    String title;
    String information;
    Integer stateCode;
    Integer cityCode;
    Integer recruitTypeCode;
    Integer maxPeople;
    Integer genderTypeCode;
    Boolean isDateAgreement;
    LocalDate hopeDate;
    String kakaoOpenChatUrl;

    List<Integer> ageGroupCodes;

    public GatheringInfo toGatheringInfo() {
        return new GatheringInfo(title,
                information,
                State.valueOf(stateCode),
                City.valueOf(cityCode),
                RecruitType.valueOf(recruitTypeCode),
                maxPeople,
                GenderType.valueOf(genderTypeCode),
                isDateAgreement,
                hopeDate,
                kakaoOpenChatUrl,
                ageGroupCodes
        );
    }

    @Builder
    public GatheringPostRequestDTO(Long contentId, String title, String information, Integer stateCode, Integer cityCode, Integer recruitTypeCode, Integer maxPeople, Integer genderTypeCode, Boolean isDateAgreement, LocalDate hopeDate, String kakaoOpenChatUrl, List<Integer> ageGroupCodes) {
        this.contentId = contentId;
        this.title = title;
        this.information = information;
        this.stateCode = stateCode;
        this.cityCode = cityCode;
        this.recruitTypeCode = recruitTypeCode;
        this.maxPeople = maxPeople;
        this.genderTypeCode = genderTypeCode;
        this.isDateAgreement = isDateAgreement;
        this.hopeDate = hopeDate;
        this.kakaoOpenChatUrl = kakaoOpenChatUrl;
        this.ageGroupCodes = ageGroupCodes;
    }
}
