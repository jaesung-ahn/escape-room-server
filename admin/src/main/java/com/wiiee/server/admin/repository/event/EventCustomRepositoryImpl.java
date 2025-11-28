package com.wiiee.server.admin.repository.event;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.common.domain.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.wiiee.server.common.domain.event.QEvent.event;

@RequiredArgsConstructor
@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Event> findAllByEnableEvent() {

        List<Event> events = jpaQueryFactory
                .selectFrom(event)
                .where(event.isOperated.eq(true))
                .fetch();

        return events;
    }
}
