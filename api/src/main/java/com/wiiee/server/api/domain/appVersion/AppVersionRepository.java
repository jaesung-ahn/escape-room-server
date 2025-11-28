package com.wiiee.server.api.domain.appVersion;

import com.wiiee.server.api.infrastructure.repository.appVersion.AppVersionCustomRepository;
import com.wiiee.server.common.domain.appVersion.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long>, AppVersionCustomRepository {

}
