package com.wiiee.server.api.infrastructure.repository.appVersion;

import com.wiiee.server.api.application.common.InitInformationRequestDTO;
import com.wiiee.server.common.domain.appVersion.AppVersion;

public interface AppVersionCustomRepository {

    AppVersion findbyVersionInfo(InitInformationRequestDTO requestDTO);
}
