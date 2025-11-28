package com.wiiee.server.api.application.notice;

import com.wiiee.server.common.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class NoticeSimpleListModel {

    @Schema(description = "공지사항 아이디")
    Long id;
    @Schema(description = "공지사항 제목")
    String title;

    @Schema(description = "공지사항 등록일")
    LocalDate createdAt;

    public static NoticeSimpleListModel fromNotice(Notice notice) {
        return new NoticeSimpleListModel(
                notice.getId(),
                notice.getTitle(),
                notice.getCreatedAt().toLocalDate());
    }

}