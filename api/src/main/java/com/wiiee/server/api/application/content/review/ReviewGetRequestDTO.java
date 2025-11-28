package com.wiiee.server.api.application.content.review;

import com.wiiee.server.api.application.common.PageRequestDTO;
import lombok.*;

@Setter
@Getter
@ToString
public class ReviewGetRequestDTO extends PageRequestDTO {

    Integer stateCode;
    Integer cityCode;

    @Builder
    public ReviewGetRequestDTO(Integer page, Integer size, Integer stateCode, Integer cityCode) {
        super(page, size);
        this.stateCode = stateCode;
        this.cityCode = cityCode;
    }

    public ReviewGetRequestDTO(Integer stateCode, Integer cityCode, PageRequestDTO dto) {
        super(dto.getPage(), dto.getSize());
        this.stateCode = stateCode;
        this.cityCode = cityCode;
    }

    protected ReviewGetRequestDTO() {
    }

}