package com.wiiee.server.api.domain.event;

import com.wiiee.server.api.application.event.EventGetRequestDTO;
import com.wiiee.server.api.infrastructure.repository.event.EventCustomRepository;
import com.wiiee.server.common.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {
    Page<Event> findAllByEventGetRequestDTO(EventGetRequestDTO dto, Pageable pageable);

}