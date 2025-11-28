package com.wiiee.server.api.infrastructure.repository.appVersion;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.common.InitInformationRequestDTO;
import com.wiiee.server.common.domain.appVersion.AppVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.wiiee.server.common.domain.appVersion.QAppVersion.appVersion;

@RequiredArgsConstructor
@Repository
public class AppVersionCustomRepositoryImpl implements AppVersionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public AppVersion findbyVersionInfo(InitInformationRequestDTO requestDTO) {

        Integer maxVersionCode = jpaQueryFactory.select(appVersion.versionCode.max())
                .from(appVersion)
                .where(appVersion.appOs.eq(requestDTO.getAppOs()))
                .fetchOne();


        // 해당 OS의 가장 최신 정보 조회
        return jpaQueryFactory.selectFrom(appVersion)
                .where(appVersion.appOs.eq(requestDTO.getAppOs())
                        ,appVersion.versionCode.eq(maxVersionCode)
                )
                .orderBy(appVersion.versionCode.desc())
                .fetchOne();
    }
}
