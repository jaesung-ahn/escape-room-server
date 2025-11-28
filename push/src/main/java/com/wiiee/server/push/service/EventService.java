package com.wiiee.server.push.service;

import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.push.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 이벤트입니다."));
    }

}
