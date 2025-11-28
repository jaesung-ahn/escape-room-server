package com.wiiee.server.admin.form;

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

}
