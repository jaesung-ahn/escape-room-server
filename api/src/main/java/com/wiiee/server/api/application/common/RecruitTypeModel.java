package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.gathering.RecruitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecruitTypeModel {
    private Integer code;
    private String name;

    public RecruitTypeModel(RecruitType recruitType) {
        this.code = recruitType.getCode();
        this.name = recruitType.getName();
    }
}
