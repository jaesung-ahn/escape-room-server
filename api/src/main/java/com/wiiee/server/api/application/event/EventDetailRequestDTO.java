package com.wiiee.server.api.application.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class EventDetailRequestDTO {

    @Schema(description = "이벤트 아이디")
    @NotNull(message = "이벤트 아이디는 필수 파라미터입니다.")
    Long eventId;

}