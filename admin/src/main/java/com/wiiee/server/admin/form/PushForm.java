package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class PushForm {

    public Long id;

    private String title;
    private String pushContent;
    private int pushTypeCode;

    private String targetOs;


    private int successCnt;
    private int failCnt;

    private Long eventId;
    public LocalDateTime createdAt;
}
