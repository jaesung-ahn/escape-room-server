package com.wiiee.server.api.infrastructure.repository.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.event.EventGetRequestDTO;
import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.common.domain.event.EventLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.wiiee.server.common.domain.event.QEvent.event;

@RequiredArgsConstructor
@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {


    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Event> findAllByEventGetRequestDTO(EventGetRequestDTO dto, Pageable pageable) {
        final var list = jpaQueryFactory.selectFrom(event)
                .where(
                        eventLocationEq(dto.getEventLocationCode()),

                        event.isOperated.eq(true),
                        event.startDate.loe(LocalDateTime.now()),
                        event.endDate.goe(LocalDateTime.now())
                )
                .orderBy(event.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(list.getResults(), pageable, list.getTotal());
    }

    private BooleanExpression eventLocationEq(Integer eventLocationCode) {
        return eventLocationCode != null ? event.eventLocation.eq(EventLocation.valueOf(eventLocationCode)) : null;
    }

    @Override
    public List<Event> findAllEvents() {

        return jpaQueryFactory.selectFrom(event)
                .where(
                        event.isOperated.eq(true),
                        event.startDate.loe(LocalDateTime.now()),
                        event.endDate.goe(LocalDateTime.now())
                )
                .orderBy(event.createdAt.desc())
                .fetch();
    }

    @Override
    public Event findByEventId(Long eventId) {
        return jpaQueryFactory.selectFrom(event)
                .where(event.id.eq(eventId),
                        event.isOperated.eq(true))
                .fetchOne();
    }
}