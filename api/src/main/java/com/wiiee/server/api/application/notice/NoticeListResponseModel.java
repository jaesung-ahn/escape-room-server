package com.wiiee.server.api.application.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoticeListResponseModel {

    @Schema(description = "공지사항 리스트")
    List<NoticeSimpleListModel> notices;

    @Builder
    public NoticeListResponseModel(List<NoticeSimpleListModel> notices) {
        this.notices = notices;
    }
}
