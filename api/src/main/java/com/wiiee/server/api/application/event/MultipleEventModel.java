package com.wiiee.server.api.application.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;

@Value
public class MultipleEventModel {

    @Schema(description = "이벤트 리스트")
    List<EventModel> events;

    @Schema(description = "표출 개수")
    int count;

    @Schema(description = "다음 페이지 유무")
    boolean hasNext;

    public static MultipleEventModel fromEventsAndHasNext(List<EventModel> events, Boolean hasNext) {
        return new MultipleEventModel(events, events.size(), hasNext);
    }

}