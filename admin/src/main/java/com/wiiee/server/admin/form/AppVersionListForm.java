package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.appVersion.AppOs;
import com.wiiee.server.common.domain.appVersion.SelectionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppVersionListForm extends DefaultForm {

    private AppOs appOs;
    private String version;
    private Integer versionCode;
    private SelectionType selectionType;
    private String latestStatus;
    private String updateContent;
    private String createdDate;

    @Builder
    public AppVersionListForm(Long id, AppOs appOs, String version, Integer versionCode, SelectionType selectionType,
                              String latestStatus, String updateContent, String createdDate) {
        this.id = id;
        this.appOs = appOs;
        this.version = version;
        this.versionCode = versionCode;
        this.selectionType = selectionType;
        this.latestStatus = latestStatus;
        this.updateContent = updateContent;
        this.createdDate = createdDate;
    }
}
