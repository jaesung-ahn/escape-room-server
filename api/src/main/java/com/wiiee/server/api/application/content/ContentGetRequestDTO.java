package com.wiiee.server.api.application.content;

import com.wiiee.server.api.application.common.PageRequestDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
public class ContentGetRequestDTO extends PageRequestDTO {

    Long companyId;
    String name;
    Integer stateCode;
    Integer cityCode;
    List<Integer> genreCodes;
    Integer difficultyCode;
    Integer escapeTypeCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime from;
    Long exceptContentId;

    ContentOrderType contentOrderType;

    @Builder
    public ContentGetRequestDTO(Integer page, Integer size, Long companyId, String name, Integer stateCode, Integer cityCode, List<Integer> genreCodes, Integer difficultyCode, Integer escapeTypeCode, LocalDateTime from, Long exceptContentId, ContentOrderType contentOrderType) {
        super(page, size);
        this.companyId = companyId;
        this.name = name;
        this.stateCode = stateCode;
        this.cityCode = cityCode;
        this.genreCodes = genreCodes;
        this.difficultyCode = difficultyCode;
        this.escapeTypeCode = escapeTypeCode;
        this.from = from;
        this.exceptContentId = exceptContentId;

        if (contentOrderType == null) {
            this.contentOrderType = ContentOrderType.LATEST;
        }
        else {
            this.contentOrderType = contentOrderType;
        }
    }

}