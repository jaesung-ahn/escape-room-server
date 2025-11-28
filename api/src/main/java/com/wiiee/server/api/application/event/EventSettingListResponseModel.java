package com.wiiee.server.api.application.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventSettingListResponseModel {

    @Schema(description = "이벤트 리스트")
    List<EventSimpleListModel> events;

    @Builder
    public EventSettingListResponseModel(List<EventSimpleListModel> events) {
        this.events = events;
    }
}