package com.wiiee.server.admin.repository.event;

import com.wiiee.server.common.domain.event.Event;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCustomRepository {

    List<Event> findAllByEnableEvent();
}
