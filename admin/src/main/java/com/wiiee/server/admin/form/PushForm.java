package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.push.PushHistory;
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

    public static PushForm from(PushHistory pushHistory) {
        PushForm form = new PushForm();
        form.setId(pushHistory.getId());
        form.setTitle(pushHistory.getTitle());
        form.setPushContent(pushHistory.getPushContent());
        form.setPushTypeCode(pushHistory.getPushType().getCode());
        form.setTargetOs(pushHistory.getTargetOs());
        form.setSuccessCnt(pushHistory.getSuccessCnt());
        form.setFailCnt(pushHistory.getFailCnt());
        form.setEventId(pushHistory.getEventId());
        form.setCreatedAt(pushHistory.getCreatedAt());
        return form;
    }
}
