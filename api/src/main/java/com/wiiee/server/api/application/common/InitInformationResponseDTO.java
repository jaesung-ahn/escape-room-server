package com.wiiee.server.api.application.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitInformationResponseDTO {

    private AppVersionModel appVersionModel;

    @Builder
    public InitInformationResponseDTO(AppVersionModel appVersionModel) {
        this.appVersionModel = appVersionModel;
    }
}
