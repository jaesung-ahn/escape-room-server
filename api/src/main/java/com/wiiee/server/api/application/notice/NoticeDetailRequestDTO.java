package com.wiiee.server.api.application.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class NoticeDetailRequestDTO {

    @Schema(description = "공지사항 아이디")
    @NotNull(message = "공지사항 아이디는 필수 파라미터입니다.")
    Long noticeId;

}