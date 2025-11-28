package com.wiiee.server.api.domain.event;

import com.wiiee.server.api.application.event.*;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.common.domain.event.Event;
import com.wiiee.server.common.domain.event.EventLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final ImageService imageService;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public MultipleEventModel getEvents(EventGetRequestDTO dto) {
        final var list = eventRepository.findAllByEventGetRequestDTO(dto, PageRequest.of(dto.getPage() - 1, dto.getSize()));
        return MultipleEventModel.fromEventsAndHasNext(getEventModelByEvents(list.getContent()), list.hasNext());
    }

    private List<EventModel> getEventModelByEvents(List<Event> events) {
        return events.stream().map(event ->
                EventModel.fromEventAndImage(event,
                        imageService.getImageById(OptionalLong.of(event.getBannerImgId()).orElse(0L)))).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventModel> getAllEvents() {
        return getEventModelByEvents(eventRepository.findAllEvents());
    }

    public List<EventModel> getEventModelByEventLocation(List<EventModel> events, EventLocation eventLocation) {
        return events.stream().filter(event ->
                    event.getEventLocation().equals(eventLocation)
                ).collect(Collectors.toList());
    }

    /**
     * 이벤트 상세 조회
     */
    @Transactional(readOnly = true)
    public EventDetailModel getEventDetail(Long eventId) {

        Event findEvent = eventRepository.findByEventId(eventId);

        return EventDetailModel.fromEvent(findEvent);
    }

    /**
     * 테스트용 이벤트 등록(등록은 어드민에서만 해야 함)
     */
    @Transactional
    public Event createTestEvent() {
        Event event = new Event(true, "테스트 이벤트", LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                EventLocation.MAIN_BANNER, 0L, "이벤트 내용", "요약 내용");
        return eventRepository.save(event);
    }

    /**
     * 이벤트 리스트 조회(설정화면)
     */
    @Transactional
    public EventSettingListResponseModel getEventSettingList() {
        List<Event> allEvents = eventRepository.findAllEvents();
        List<EventSimpleListModel> eventList = allEvents.stream().map(event -> EventSimpleListModel.fromEvent(event)).collect(Collectors.toList());
        return EventSettingListResponseModel.builder().events(eventList).build();
    }
}