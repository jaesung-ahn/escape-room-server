package com.wiiee.server.api.application.content;

import com.wiiee.server.common.domain.content.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class MultipleContentModel {

    @Schema(description = "컨텐츠 리스트")
    List<ContentSimpleModel> contents;
    @Schema(description = "전체 개수")
    long count;
    @Schema(description = "다음 페이지 유무")
    boolean hasNext;

    public static MultipleContentModel fromContentsAndHasNext(List<ContentSimpleModel> contents, Page<Content> contentPage) {
        return new MultipleContentModel(contents, contentPage.getTotalElements(), contentPage.hasNext());
    }

}