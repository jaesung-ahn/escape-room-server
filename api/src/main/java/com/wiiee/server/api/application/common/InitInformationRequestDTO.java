package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.appVersion.AppOs;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitInformationRequestDTO {

    @Schema(description = "앱 OS(ANDROID, IOS)" )
    private AppOs appOs;
    @Schema(description = "앱 사용 버전")
    private String version;
    @Schema(description = "앱 사용 버전 코드")
    private Integer versionCode;

    @Schema(description = "유저 아이디(버전 등 정보 수집용)")
    private Integer userId;

    @Builder
    public InitInformationRequestDTO(AppOs appOs, String version, Integer versionCode, Integer userId) {
        this.appOs = appOs;
        this.version = version;
        this.versionCode = versionCode;
        this.userId = userId;
    }
}
