package com.wiiee.server.admin.repository.event;

import com.wiiee.server.common.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

//    private final EntityManager em;
}
