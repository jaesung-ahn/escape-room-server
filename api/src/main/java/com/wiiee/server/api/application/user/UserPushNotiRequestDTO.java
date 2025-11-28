package com.wiiee.server.api.application.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserPushNotiRequestDTO {

    @Schema(description = "놀거리 알림")
    Boolean isPushContent;
    @Schema(description = "동행 알림")
    Boolean isPushGathering;
    @Schema(description = "서비스 알림")
    Boolean isPushEvent;
    @Schema(description = "서비스 마케팅 수신 동의")
    Boolean isAgreeServiceMarketing;

}
