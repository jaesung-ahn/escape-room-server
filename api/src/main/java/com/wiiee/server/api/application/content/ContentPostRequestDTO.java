package com.wiiee.server.api.application.content;

import com.wiiee.server.api.application.content.price.PricePostRequestDTO;
import com.wiiee.server.common.domain.content.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ContentPostRequestDTO {

    Long companyId;
    String name;
    Integer genreCode;
    List<Long> imageIds;
    Integer minPeople;
    Integer maxPeople;
    String information;
    Integer playTime;
    Integer activityLevelCode;
    Integer escapeTypeCode;
    Boolean isCaution;
    Integer difficultyCode;
    Boolean isNoEscapeType;
    Boolean isNew;
    LocalDate newDisplayExpirationDate;
    Boolean isOperated;

    List<PricePostRequestDTO> priceList = new ArrayList<>();

    public ContentBasicInfo toContentBasicInfo() {
        return new ContentBasicInfo(
                name,
                Genre.valueOf(genreCode),
                information,
                playTime,
                ActivityLevel.valueOf(activityLevelCode),
                EscapeType.valueOf(escapeTypeCode),
                isCaution,
                minPeople,
                maxPeople,
                Difficulty.valueOf(difficultyCode),
                imageIds,
                isNoEscapeType,
                isNew,
                newDisplayExpirationDate,
                isOperated
        );
    }

}
