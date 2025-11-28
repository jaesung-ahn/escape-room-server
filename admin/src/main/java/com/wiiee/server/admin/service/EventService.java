package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.EventForm;
import com.wiiee.server.admin.repository.event.EventCustomRepository;
import com.wiiee.server.admin.repository.event.EventRepository;
import com.wiiee.server.admin.util.DateUtil;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    private final EventCustomRepository eventCustomRepository;

    @Transactional(readOnly = true)
    public EventForm findByIdForForm(Long id) {
        Optional<Event> optEvent = eventRepository.findById(id);
        Event event = optEvent.get();

        EventForm eventForm = modelMapper.map(event, EventForm.class);
        Long bannerImgId = eventForm.getBannerImgId();
        if (bannerImgId != null) {
            Image bannerImg = imageService.getImageById(bannerImgId);
            if (bannerImg.getId() > 0 && bannerImg.getUrl() != null) {
                eventForm.setBannerImgUrl(bannerImg.getUrl());
            }
        }

        eventForm.setDisplayStartDate(DateUtil.formatDate(event.getStartDate()));
        eventForm.setDisplayEndDate(DateUtil.formatDate(event.getEndDate()));

        return eventForm;
    }

    public List<EventForm> findAll() {

        List<Event> events = eventRepository.findAll();
        List<EventForm> eventForms =
                events.stream().map(p -> modelMapper.map(p, EventForm.class))
                        .collect(Collectors.toList());

        eventForms.stream().forEach(eventForm -> {

            eventForm.setDisplayStartDate(
                    DateUtil.formatDate(eventForm.getStartDate())
            );
            log.debug(String.valueOf("eventForm = " + eventForm));
        });

        return eventForms;
    }

    public List<EventForm> findAllByEnableEvent() {
        List<Event> events = eventCustomRepository.findAllByEnableEvent();
        List<EventForm> eventForms = events.stream().map(event -> modelMapper.map(event, EventForm.class))
                .collect(Collectors.toList());

        eventForms.stream().forEach(eventForm -> {

            eventForm.setDisplayStartDate(DateUtil.formatDate(eventForm.getStartDate()));
            eventForm.setDisplayEndDate(DateUtil.formatDate(eventForm.getEndDate()));

        });
        return eventForms;
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
