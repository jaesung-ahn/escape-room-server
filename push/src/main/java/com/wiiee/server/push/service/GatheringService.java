package com.wiiee.server.push.service;

import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.push.repository.GatheringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GatheringService {

    private final GatheringRepository gatheringRepository;

    @Transactional(readOnly = true)
    public Gathering findById(Long id) {
        return gatheringRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 동행 모집입니다."));
    }

}
