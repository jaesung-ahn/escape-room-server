package com.wiiee.server.common.domain.recommendation;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.user.UserGenderType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class RecommendationInfo {

    private String categoryName;

    @Enumerated(value = EnumType.STRING)
    private State state;

    @Enumerated(value = EnumType.STRING)
    private City city;

    @Enumerated(value = EnumType.STRING)
    private UserGenderType userGenderType;

    @Enumerated(value = EnumType.STRING)
    private AgeGroup ageGroup;

    public RecommendationInfo(String categoryName, State state, City city, UserGenderType userGenderType, AgeGroup ageGroup) {
        this.categoryName = categoryName;
        this.state = state;
        this.city = city;
        this.userGenderType = userGenderType;
        this.ageGroup = ageGroup;
    }
}
