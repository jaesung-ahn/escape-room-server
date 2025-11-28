package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.EventForm;
import com.wiiee.server.admin.form.PushForm;
import com.wiiee.server.admin.form.PushHistoryListForm;
import com.wiiee.server.admin.repository.PushHistoryRepository;
import com.wiiee.server.common.domain.push.PushHistory;
import com.wiiee.server.common.domain.push.PushType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PushHistoryService {

    private final PushHistoryRepository pushHistoryRepository;

    private final ModelMapper modelMapper;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public List<PushHistoryListForm> findAllByPushHistoryListReqDTO() {
        List<PushHistory> pushHistoryList = pushHistoryRepository.findAll();
        return pushHistoryList.stream().map(pushHistory -> modelMapper.map(pushHistory, PushHistoryListForm.class))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public PushForm findByIdForForm(Long id) {
        PushHistory pushHistory = pushHistoryRepository.findById(id).get();
        PushForm pushForm = modelMapper.map(pushHistory, PushForm.class);
        pushForm.setPushTypeCode(pushHistory.getPushType().getCode());
        if (pushHistory.getEventId() != 0) {
            EventForm eventForm = eventService.findByIdForForm(pushHistory.getEventId());
            pushForm.setEventId(eventForm.getId());
        }

        return pushForm;
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
