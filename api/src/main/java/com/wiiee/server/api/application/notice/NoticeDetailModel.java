package com.wiiee.server.api.application.notice;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoticeDetailModel {

    @Schema(description = "공지사항 아이디")
    Long id;
    @Schema(description = "공지사항 제목")
    String title;

    @Schema(description = "공지사항 등록일시")
    String createdAt;
    @Schema(description = "공지사항 내용")
    String noticeContent;

    public static NoticeDetailModel fromNotice(Notice notice) {
        return new NoticeDetailModel(
                notice.getId(),
                notice.getTitle(),
                LocalDateTimeUtil.getFormattingLocalDateTime(notice.getCreatedAt()),
                notice.getNoticeContent()
        );
    }
}
