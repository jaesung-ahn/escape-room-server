package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import com.wiiee.server.common.domain.gathering.GenderType;
import com.wiiee.server.common.domain.gathering.RecruitType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class GatheringForm extends DefaultForm {

    private String leader;

    @NotEmpty(message = "제목은 필수 입니다")
    private String title;

    private String information;

    private String companyAndContentName;

    private Long contentId;

    private GatheringStatus gatheringStatus;

    private State state;

    private City city;

    private RecruitType recruitType;

    private Integer maxPeople;

    private List<Integer> ageGroups = new ArrayList<>();

    private GenderType genderType;

    private Boolean isDateAgreement;

    private LocalDate hopeDate;

    private String kakaoOpenChatUrl;

    // 동행 실제 일자
    private LocalDate realGatherDate;

    // 방문자 수
    private Integer hitCount = 0;

    // 가격 - 사람 수
    private Integer peopleNumber;

    // 가격 - 해당 가격
    private Integer price;

    public GatheringForm() {
    }

    public GatheringForm(String title, String leader, City city) {
        this.leader = leader;
        this.title = title;
        this.city = city;
    }
}

