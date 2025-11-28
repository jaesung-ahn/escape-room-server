package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserPushNotiResponseDTO {

    @Schema(description = "놀거리 알림")
    Boolean isPushContent;
    @Schema(description = "동행 알림")
    Boolean isPushGathering;
    @Schema(description = "서비스 알림")
    Boolean isPushEvent;
    @Schema(description = "서비스 마케팅 수신 동의")
    Boolean isAgreeServiceMarketing;

    @Schema(description = "서비스 마케팅 수신 동의 날짜(상태는 위 수신동의 값)")
    LocalDate updatedAgreeServiceMarketingDate;

    @Builder
    public UserPushNotiResponseDTO(Profile profile) {
        this.isPushContent = profile.isPushContent();
        this.isPushGathering = profile.isPushGathering();
        this.isPushEvent = profile.isPushEvent();
        this.isAgreeServiceMarketing = profile.isAgreeServiceMarketing();
        if (profile.getUpdatedAgreeServiceMarketingDate() != null) {
            this.updatedAgreeServiceMarketingDate = LocalDate.from(profile.getUpdatedAgreeServiceMarketingDate());
        }
    }
}
