package com.wiiee.server.api.application.company;

import com.wiiee.server.api.application.common.PageRequestDTO;
import lombok.*;

@Setter
@Getter
@ToString
public class CompanyGetRequestDTO extends PageRequestDTO {

    String name;
    Integer stateCode;
    Integer cityCode;

    @Builder
    public CompanyGetRequestDTO(Integer page, Integer size, String name, Integer stateCode, Integer cityCode) {
        super(page, size);
        this.name = name;
        this.stateCode = stateCode;
        this.cityCode = cityCode;
    }

}