package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.gathering.AgeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AgeGroupInfoModel {

    private Integer code;
    private String name;

    public AgeGroupInfoModel(AgeGroup ageGroup) {
        this.code = ageGroup.getCode();
        this.name = ageGroup.getName();
    }
}
