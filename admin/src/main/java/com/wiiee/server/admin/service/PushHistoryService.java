package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.PushForm;
import com.wiiee.server.admin.form.PushHistoryListForm;
import com.wiiee.server.admin.repository.PushHistoryRepository;
import com.wiiee.server.common.domain.push.PushHistory;
import com.wiiee.server.common.domain.push.PushType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PushHistoryService {

    private final PushHistoryRepository pushHistoryRepository;

    @Transactional(readOnly = true)
    public List<PushHistoryListForm> findAllByPushHistoryListReqDTO() {
        List<PushHistory> pushHistoryList = pushHistoryRepository.findAll();
        return pushHistoryList.stream().map(PushHistoryListForm::from)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public PushForm findByIdForForm(Long id) {
        PushHistory pushHistory = pushHistoryRepository.findById(id).get();
        return PushForm.from(pushHistory);
    }

    public PushHistory savePushHistory(PushForm pushForm) {

        return pushHistoryRepository.save(new PushHistory(pushForm.getTitle(),
                pushForm.getPushContent(),
                PushType.valueOf(pushForm.getPushTypeCode()),
                pushForm.getTargetOs(),
                pushForm.getEventId()
        ));
    }
}
