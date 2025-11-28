package com.wiiee.server.api.domain.appVersion;

import com.wiiee.server.api.application.common.AppVersionModel;
import com.wiiee.server.api.application.common.InitInformationRequestDTO;
import com.wiiee.server.common.domain.appVersion.AppVersion;
import com.wiiee.server.common.domain.appVersion.SelectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    /**
     * 앱버전 체크
     * 유저 버전 정보를 받아서 최신 유무 등 리턴
     */
    @Transactional(readOnly = true)
    public AppVersionModel checkLatestVersion(InitInformationRequestDTO requestDTO) {
        AppVersion appVersion = appVersionRepository.findbyVersionInfo(requestDTO);
        SelectionType selectionType = SelectionType.NOTHING;
        if (requestDTO.getVersionCode() < appVersion.getVersionCode()) {
            selectionType = appVersion.getSelectionType();
        }

        return AppVersionModel.builder()
                        .appOs(appVersion.getAppOs())
                        .latestVersion(appVersion.getVersion())
                        .latestVersionCode(appVersion.getVersionCode())
                        .resultSelectionType(selectionType)
                .build();
    }
}
