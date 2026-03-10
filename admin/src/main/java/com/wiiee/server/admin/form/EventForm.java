package com.wiiee.server.admin.form;

import com.wiiee.server.admin.util.DateUtil;
import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.common.domain.event.EventLocation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class EventForm extends DefaultForm {

    private String title;
    private String bannerDescription;
    private Boolean isOperated;
    private LocalDateTime startDate;
    private String displayStartDate;
    private LocalDateTime endDate;
    private String displayEndDate;
    private EventLocation eventLocation;

    private Long bannerImgId;
    private String bannerImgUrl;
    private int hitCount = 0;
    private String eventContent;

    public static EventForm from(Event event) {
        EventForm form = new EventForm();
        form.setId(event.getId());
        form.setCreatedAt(event.getCreatedAt());
        form.setTitle(event.getTitle());
        form.setBannerDescription(event.getBannerDescription());
        form.setIsOperated(event.getIsOperated());
        form.setStartDate(event.getStartDate());
        form.setDisplayStartDate(DateUtil.formatDate(event.getStartDate()));
        form.setEndDate(event.getEndDate());
        form.setDisplayEndDate(DateUtil.formatDate(event.getEndDate()));
        form.setEventLocation(event.getEventLocation());
        form.setBannerImgId(event.getBannerImgId());
        form.setHitCount(event.getHitCount());
        form.setEventContent(event.getEventContent());
        return form;
    }
}
