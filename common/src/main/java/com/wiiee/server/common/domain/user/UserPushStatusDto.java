package com.wiiee.server.common.domain.user;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@NoArgsConstructor
@Setter
public class UserPushStatusDto {

    public Boolean isPushContent;

    public Boolean isPushGathering;

    public Boolean isPushEvent;

    public Boolean isAgreeServiceMarketing;

    public UserPushStatusDto(boolean isPushContent, boolean isPushGathering, boolean isPushEvent, boolean isAgreeServiceMarketing) {
        this.isPushContent = isPushContent;
        this.isPushGathering = isPushGathering;
        this.isPushEvent = isPushEvent;
        this.isAgreeServiceMarketing = isAgreeServiceMarketing;
    }

    public Optional<Boolean> getIsPushContent() {
        return Optional.ofNullable(isPushContent);
    }

    public Optional<Boolean> getIsPushGathering() {
        return Optional.ofNullable(isPushGathering);
    }

    public Optional<Boolean> getIsPushEvent() {
        return Optional.ofNullable(isPushEvent);
    }

    public Optional<Boolean> getIsAgreeServiceMarketing() {
        return Optional.ofNullable(isAgreeServiceMarketing);
    }
}
