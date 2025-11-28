package com.wiiee.server.api.application.notice;

import com.wiiee.server.api.application.faq.FaqListResponseModel;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.faq.FaqService;
import com.wiiee.server.api.domain.notice.NoticeService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Notice api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notice")
public class NoticeRestController {

    private final NoticeService noticeService;
    private final FaqService faqService;

    @Operation(summary = "공지사항 리스트 조회(설정화면)", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<NoticeListResponseModel> getNoticeList() {
        return ApiResponse.success(noticeService.getNoticeAll());
    }

    @Operation(summary = "공지사항 상세 조회", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/detail", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<NoticeDetailModel> getNoticeDetail(@Validated @RequestBody NoticeDetailRequestDTO dto,
                                                          @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(noticeService.getNoticeDetail(dto.getNoticeId()));
    }

    @Operation(summary = "자주묻는질문 리스트 조회", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/faq-list", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<FaqListResponseModel> getFaqList() {
        return ApiResponse.success(faqService.getFaqAll());
    }
}
