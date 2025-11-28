package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class WbtiListForm {

    public Long id;
    public LocalDateTime createdAt;

    private String name;
}
