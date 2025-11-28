package com.wiiee.server.api.infrastructure.repository.event;

import com.wiiee.server.api.application.event.EventGetRequestDTO;
import com.wiiee.server.common.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventCustomRepository {

    Page<Event> findAllByEventGetRequestDTO(EventGetRequestDTO dto, Pageable pageable);

    List<Event> findAllEvents();

    Event findByEventId(Long eventId);
}