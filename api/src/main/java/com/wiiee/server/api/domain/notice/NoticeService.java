package com.wiiee.server.api.domain.notice;

import com.wiiee.server.api.application.notice.NoticeDetailModel;
import com.wiiee.server.api.application.notice.NoticeListResponseModel;
import com.wiiee.server.api.application.notice.NoticeSimpleListModel;
import com.wiiee.server.common.domain.notice.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public NoticeListResponseModel getNoticeAll(){

        List<Notice> noticeList = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return NoticeListResponseModel.builder().notices(
                noticeList.stream().map(notice -> NoticeSimpleListModel.fromNotice(notice)).collect(Collectors.toList())
        ).build();
    }

    @Transactional(readOnly = true)
    public NoticeDetailModel getNoticeDetail(Long noticeId) {
        return NoticeDetailModel.fromNotice(
                noticeRepository.findById(noticeId).orElseThrow()
        );
    }
}
