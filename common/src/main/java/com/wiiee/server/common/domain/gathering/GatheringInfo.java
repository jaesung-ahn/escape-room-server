package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class GatheringInfo {

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String information;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GatheringStatus gatheringStatus = GatheringStatus.RECRUITING;

    @Enumerated(value = EnumType.STRING)
    private State state;

    @Enumerated(value = EnumType.STRING)
    private City city;

    // 모집 방식
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RecruitType recruitType;

    // 메이트 수(최대 8명)
    private Integer maxPeople;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "int[]")
    private List<Integer> ageGroupCodes = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GenderType genderType;

    // 일시 협의 여부
    private Boolean isDateAgreement;

    // 동행 희망일
    private LocalDate hopeDate;

    private String kakaoOpenChatUrl;

    // 동행 실제 일자
    private LocalDate realGatherDate;

    @Column(columnDefinition = "integer default 0")
    // 방문자 수
    private Integer hitCount = 0;

    public GatheringInfo(String title, String information, State state, City city, RecruitType recruitType, Integer maxPeople, GenderType genderType, Boolean isDateAgreement, LocalDate hopeDate, String kakaoOpenChatUrl, List<Integer> ageGroupCodes) {
        this.title = title;
        this.information = information;
        this.state = state;
        this.city = city;
        this.recruitType = recruitType;
        this.maxPeople = maxPeople;
        this.genderType = genderType;
        this.isDateAgreement = isDateAgreement;
        this.hopeDate = hopeDate;
        this.kakaoOpenChatUrl = kakaoOpenChatUrl;
        this.ageGroupCodes = ageGroupCodes;
    }

    public GatheringInfo(String title, String information, State state, City city, RecruitType recruitType, Integer maxPeople,
                         GenderType genderType, Boolean isDateAgreement, LocalDate hopeDate, String kakaoOpenChatUrl,
                         GatheringStatus gatheringStatus) {
        this.title = title;
        this.information = information;
        this.state = state;
        this.city = city;
        this.recruitType = recruitType;
        this.maxPeople = maxPeople;
        this.genderType = genderType;
        this.isDateAgreement = isDateAgreement;
        this.hopeDate = hopeDate;
        this.kakaoOpenChatUrl = kakaoOpenChatUrl;
        this.gatheringStatus = gatheringStatus;
    }

    public void updateGatheringStatus(GatheringStatus gatheringStatus) {
        this.gatheringStatus = gatheringStatus;
    }

    public void updateMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }

    public void updateGatheringInfo(String title, String information, Integer stateCode, Integer cityCode, Integer recruitTypeCode,
                                    Integer maxPeople, Integer genderTypeCode, Boolean isDateAgreement,
                                    LocalDate hopeDate, String kakaoOpenChatUrl) {
        this.title = title;
        this.information = information;
        this.state = State.valueOf(stateCode);
        this.city = City.valueOf(cityCode);
        this.recruitType = RecruitType.valueOf(recruitTypeCode);
        this.maxPeople = maxPeople;
        this.genderType = GenderType.valueOf(genderTypeCode);
        this.isDateAgreement = isDateAgreement;
        this.hopeDate = hopeDate;
        this.kakaoOpenChatUrl = kakaoOpenChatUrl;
    }
}
