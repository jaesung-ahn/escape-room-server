package com.wiiee.server.api.application.event;

import com.wiiee.server.common.domain.event.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class EventSimpleListModel {

    @Schema(description = "이벤트 아이디")
    Long id;
    @Schema(description = "이벤트 제목")
    String title;

    @Schema(description = "이벤트 등록일")
    LocalDate createdAt;

    public static EventSimpleListModel fromEvent(Event event) {
        return new EventSimpleListModel(
                event.getId(),
                event.getTitle(),
                event.getCreatedAt().toLocalDate());
    }

}