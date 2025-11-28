package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DefaultForm {

    public Long id;
    public LocalDateTime createdAt;
}
