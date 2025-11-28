package com.wiiee.server.api.application.event;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.common.domain.event.EventLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventModel {

    @Schema(description = "이벤트 아이디")
    Long id;
    @Schema(description = "이벤트 제목")
    String title;
    @Schema(description = "시작일")
    String startDate;
    @Schema(description = "종료일")
    String endDate;
    @Schema(description = "배너 설명")
    String bannerDescription;

    @Schema(description = "이미지")
    String imageUrl;

    @Schema(description = "이벤트 종류(MAIN_BANNER: 메인 배너, ROTATION_BANNER1: 상단 배너, ROTATION_BANNER2: 하단 배너)")
    EventLocation eventLocation;

    public static EventModel fromEventAndImage(Event event, Image image) {
        return new EventModel(
                event.getId(),
                event.getTitle(),
                LocalDateTimeUtil.getFormattingLocalDateTime(event.getStartDate()),
                LocalDateTimeUtil.getFormattingLocalDateTime(event.getEndDate()),
                event.getBannerDescription(),
                image.getUrl(),
                event.getEventLocation());
    }

}