package com.wiiee.server.api.application.content.price;

import com.wiiee.server.common.domain.content.price.ContentPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class MultiplePriceModel {

    @Schema(description = "가격 리스트")
    List<PriceModel> prices;
    @Schema(description = "총 개수")
    int count;

    public static MultiplePriceModel fromPrices(List<ContentPrice> contentPrices) {
        final var priceCollected = contentPrices.stream().map(PriceModel::fromPrice)
                .collect(Collectors.toList());
        return new MultiplePriceModel(priceCollected, priceCollected.size());
    }

}
