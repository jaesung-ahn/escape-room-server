package com.wiiee.server.api.application.event;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.event.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventDetailModel {

    @Schema(description = "이벤트 아이디")
    Long id;
    @Schema(description = "이벤트 제목")
    String title;

    @Schema(description = "이벤트 등록일시")
    String createdAt;
    @Schema(description = "이벤트 내용")
    String eventContent;


    public static EventDetailModel fromEvent(Event event) {

        return new EventDetailModel(
                event.getId(),
                event.getTitle(),
                LocalDateTimeUtil.getFormattingLocalDateTime(event.getCreatedAt()),
                event.getEventContent()
        );
    }

}