package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.EventForm;
import com.wiiee.server.admin.repository.event.EventCustomRepository;
import com.wiiee.server.admin.repository.event.EventRepository;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final ImageService imageService;

    private final EventCustomRepository eventCustomRepository;

    @Transactional(readOnly = true)
    public EventForm findByIdForForm(Long id) {
        Optional<Event> optEvent = eventRepository.findById(id);
        Event event = optEvent.get();

        EventForm eventForm = EventForm.from(event);
        Long bannerImgId = eventForm.getBannerImgId();
        if (bannerImgId != null) {
            Image bannerImg = imageService.getImageById(bannerImgId);
            if (bannerImg.getId() > 0 && bannerImg.getUrl() != null) {
                eventForm.setBannerImgUrl(bannerImg.getUrl());
            }
        }

        return eventForm;
    }

    public List<EventForm> findAll() {

        List<Event> events = eventRepository.findAll();
        return events.stream().map(EventForm::from)
                .collect(Collectors.toList());
    }

    public List<EventForm> findAllByEnableEvent() {
        List<Event> events = eventCustomRepository.findAllByEnableEvent();
        return events.stream().map(EventForm::from)
                .collect(Collectors.toList());
    }

    public void updateEvent(EventForm eventForm) {

        log.debug("updateEvent() ");
        Optional<Event> optEvent = eventRepository.findById(eventForm.getId());
        Event event = optEvent.get();

        String s = eventForm.getDisplayStartDate() + "T00:00:00";
        String e = eventForm.getDisplayEndDate() + "T23:59:00";
        eventForm.setStartDate(LocalDateTime.parse(s));
        eventForm.setEndDate(LocalDateTime.parse(e));
        event.updateEvent(
                eventForm.getIsOperated(),
                eventForm.getTitle(),
                eventForm.getStartDate(),
                eventForm.getEndDate(),
                eventForm.getEventLocation(),
                eventForm.getBannerImgId(),
                eventForm.getEventContent(),
                eventForm.getBannerDescription()
        );
        log.debug(String.valueOf("event = " + event));
        eventRepository.save(event);
    }

    public void saveEvent(EventForm eventForm) {
        String startDate = eventForm.getDisplayStartDate() + "T00:00:00";
        String endDate = eventForm.getDisplayEndDate() + "T23:59:00";

        eventForm.setStartDate(LocalDateTime.parse(startDate));
        eventForm.setEndDate(LocalDateTime.parse(endDate));

        Event event = new Event(
                eventForm.getIsOperated(),
                eventForm.getTitle(),
                eventForm.getStartDate(),
                eventForm.getEndDate(),
                eventForm.getEventLocation(),
                eventForm.getBannerImgId(),
                eventForm.getEventContent(),
                eventForm.getBannerDescription()
        );
        eventRepository.save(event);
    }
}
