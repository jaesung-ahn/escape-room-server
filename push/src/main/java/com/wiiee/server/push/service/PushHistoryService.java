package com.wiiee.server.push.service;

import com.wiiee.server.common.domain.push.PushHistory;
import com.wiiee.server.push.repository.PushHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushHistoryService {

    private final PushHistoryRepository pushHistoryRepository;

    public void updatePushHistory(Long pushHistoryId, int successCnt, int failCnt) {
        PushHistory pushHistory = pushHistoryRepository.findById(pushHistoryId).get();
        pushHistory.updatePushCnt(successCnt, failCnt);

        pushHistoryRepository.save(pushHistory);
    }
}
