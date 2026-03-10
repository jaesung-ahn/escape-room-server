package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class WbtiListForm {

    public Long id;
    public LocalDateTime createdAt;

    private String name;

    public static WbtiListForm from(Wbti wbti) {
        WbtiListForm form = new WbtiListForm();
        form.setId(wbti.getId());
        form.setCreatedAt(wbti.getCreatedAt());
        form.setName(wbti.getName());
        return form;
    }
}
