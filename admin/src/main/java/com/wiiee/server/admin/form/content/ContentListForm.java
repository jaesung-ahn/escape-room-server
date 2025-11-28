package com.wiiee.server.admin.form.content;

import com.wiiee.server.admin.util.DateUtil;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Content;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ContentListForm {

    private Long id;
    private String companyName;
    private String name;

    private State state;
    private City city;

    private boolean isOperated;
    public String createdAt;

    public static ContentListForm fromContentListForm(Content content) {
        return ContentListForm.builder()
                .id(content.getId())
                .name(content.getContentBasicInfo().getName())
                .city(content.getCompany().getBasicInfo().getCity())
                .companyName(content.getCompany().getBasicInfo().getName())
                .isOperated(content.getContentBasicInfo().getIsOperated())
                .createdAt(DateUtil.formatDate(content.getCreatedAt()))
                .build();
    }
}
