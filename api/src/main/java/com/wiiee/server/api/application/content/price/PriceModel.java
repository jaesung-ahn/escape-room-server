package com.wiiee.server.api.application.content.price;

import com.wiiee.server.common.domain.content.price.ContentPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class PriceModel {

    @Schema(description = "아이디")
    Long id;
    @Schema(description = "인원")
    Integer number;
    @Schema(description = "가격")
    Integer value;

    public static PriceModel fromPrice(ContentPrice contentPrice) {
        return PriceModel.builder()
                .id(contentPrice.getId())
                .number(contentPrice.getPeopleNumber())
                .value(contentPrice.getPrice())
                .build();
    }
}
