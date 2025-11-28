package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.common.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CityModel {
    private Integer code;
    private String name;

    public CityModel(City city) {
        this.code = city.getCode();
        this.name = city.getName();
    }
}
