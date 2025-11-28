package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.appVersion.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

}
