package com.wiiee.server.api.application.gathering;

import com.wiiee.server.api.application.common.PageRequestDTO;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GatheringGetRequestDTO extends PageRequestDTO {

    Integer stateCode;
    Integer cityCode;
    Integer recruitTypeCode;
    Integer genreCode;
    List<Integer> difficultyCodes;
    String title;

    Boolean isDeadline;
    Boolean isCompleted;

    GatheringOrderType gatheringOrderType;

    @Builder
    public GatheringGetRequestDTO(Integer page, Integer size, Integer stateCode, Integer cityCode, Integer recruitTypeCode,
                                  Integer genreCode, List<Integer> difficultyCode, String title, Boolean isDeadline,
                                  Boolean isCompleted, GatheringOrderType gatheringOrderType) {
        super(page, size);
        this.stateCode = stateCode;
        this.cityCode = cityCode;
        this.recruitTypeCode = recruitTypeCode;
        this.genreCode = genreCode;
        this.difficultyCodes = difficultyCode;
        this.title = title;
        this.isDeadline = isDeadline;
        this.isCompleted = isCompleted;
        this.gatheringOrderType = gatheringOrderType;
    }

}