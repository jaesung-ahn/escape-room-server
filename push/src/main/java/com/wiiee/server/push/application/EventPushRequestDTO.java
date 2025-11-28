package com.wiiee.server.push.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class EventPushRequestDTO {

    @Schema(required = true, description = "event id")
    private Long eventId;

    @Schema(required = true, description = "푸시 제목")
    private String title;

    @Schema(required = true, description = "푸시 내용글")
    private String pushContent;

    @Schema(required = true, description = "푸시 히스토리 id")
    private Long pushHistoryId;

    @Builder
    public EventPushRequestDTO(Long eventId, String title, String pushContent, Long pushHistoryId) {
        this.eventId = eventId;
        this.title = title;
        this.pushContent = pushContent;
        this.pushHistoryId = pushHistoryId;
    }
}
