package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.gathering.GenderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GenderTypeModel {
    private Integer code;
    private String name;

    public GenderTypeModel(GenderType genderType) {
        this.code = genderType.getCode();
        this.name = genderType.getName();
    }
}
