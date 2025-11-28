package com.wiiee.server.api.application.content.tag;

import com.wiiee.server.common.domain.content.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class TagModel {

    @Schema(description = "아이디")
    Long id;
    @Schema(description = "태그 명")
    String value;

    public static TagModel fromTag(Tag tag) {
        return TagModel.builder()
                .id(tag.getId())
                .value(tag.getValue())
                .build();
    }

}
