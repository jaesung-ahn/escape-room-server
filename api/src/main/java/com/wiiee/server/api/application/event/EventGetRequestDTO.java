package com.wiiee.server.api.application.event;

import com.wiiee.server.api.application.common.PageRequestDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EventGetRequestDTO extends PageRequestDTO {

    Integer eventLocationCode;

    @Builder
    public EventGetRequestDTO(Integer page, Integer size, Integer eventLocationCode) {
        super(page, size);
        this.eventLocationCode = eventLocationCode;
    }

}