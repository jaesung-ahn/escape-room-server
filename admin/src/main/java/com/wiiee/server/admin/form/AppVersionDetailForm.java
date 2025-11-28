package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.appVersion.AppOs;
import com.wiiee.server.common.domain.appVersion.SelectionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AppVersionDetailForm extends DefaultForm{

    private AppOs appOs;
    private String version;
    private Integer versionCode;
    private SelectionType selectionType;
    private String updateContent;

}
