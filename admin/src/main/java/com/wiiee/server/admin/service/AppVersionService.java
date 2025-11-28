package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.AppVersionDetailForm;
import com.wiiee.server.admin.form.AppVersionListForm;
import com.wiiee.server.admin.repository.AppVersionRepository;
import com.wiiee.server.admin.util.DateUtil;
import com.wiiee.server.common.domain.appVersion.AppVersion;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<AppVersionListForm> findAll() {
        List<AppVersion> appVersions = appVersionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        int maxVersionCode = appVersions.stream().mapToInt(value -> value.getVersionCode())
                .max().orElseThrow();

        return appVersions.stream().map(appVersion -> {
            String latestStatus = "이전";
            if (appVersion.getVersionCode() == maxVersionCode) {
                latestStatus = "최신";
            }

            return AppVersionListForm.builder()
                    .id(appVersion.getId())
                    .appOs(appVersion.getAppOs())
                    .version(appVersion.getVersion())
                    .versionCode(appVersion.getVersionCode())
                    .selectionType(appVersion.getSelectionType())
                    .latestStatus(latestStatus)
                    .updateContent(appVersion.getUpdateContent())
                    .createdDate(DateUtil.formatDate(appVersion.getCreatedAt()) )
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppVersionDetailForm findByIdForForm(Long appVersionId) {
        AppVersion appVersion = appVersionRepository.findById(appVersionId).get();
        return modelMapper.map(appVersion, AppVersionDetailForm.class);
    }

    @Transactional
    public void updateAppVersion(AppVersionDetailForm appVersionDetailForm) {
        AppVersion appVersion = appVersionRepository.findById(appVersionDetailForm.getId()).orElseThrow();
        appVersion.updateAppVersion(
                appVersionDetailForm.getAppOs(),
                appVersionDetailForm.getVersion(),
                appVersionDetailForm.getVersionCode(),
                appVersionDetailForm.getSelectionType(),
                appVersionDetailForm.getUpdateContent()
        );
    }

    @Transactional
    public AppVersion saveAppVersion(AppVersionDetailForm appVersionDetailForm) {
        AppVersion appVersion = new AppVersion(
                appVersionDetailForm.getAppOs(),
                appVersionDetailForm.getVersion(),
                appVersionDetailForm.getVersionCode(),
                appVersionDetailForm.getSelectionType(),
                appVersionDetailForm.getUpdateContent()
        );
        return appVersionRepository.save(appVersion);
    }
}
