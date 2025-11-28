package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.appVersion.AppOs;
import com.wiiee.server.common.domain.appVersion.SelectionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppVersionModel {

    @Schema(description = "앱 OS")
    private AppOs appOs;
    @Schema(description = "최신 버전")
    private String latestVersion;
    @Schema(description = "최신 버전 코드")
    private Integer latestVersionCode;
    @Schema(description = "버전에 따른 처리 타입")
    private SelectionType resultSelectionType;

    @Builder
    public AppVersionModel(AppOs appOs, String latestVersion, Integer latestVersionCode, SelectionType resultSelectionType) {
        this.appOs = appOs;
        this.latestVersion = latestVersion;
        this.latestVersionCode = latestVersionCode;
        this.resultSelectionType = resultSelectionType;
    }
}

