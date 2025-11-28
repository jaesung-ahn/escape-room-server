package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.form.EventForm;
import com.wiiee.server.admin.service.EventService;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.event.EventLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping(path="/admin/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/list")
    public String getEventListPage(Model model) {
        log.debug("call getEventListPage()");

        model.addAttribute("event_list", eventService.findAll());

        return "event/event_list";
    }

    @GetMapping({"/detail"})
    public String getEventDetailPage(@RequestParam(value="eventId") Long eventId,
                                     Model model) {
//         INSERT INTO "public"."event" ("event_id", "created_at", "updated_at", "deleted", "deleted_at", "banner_img_id", "end_date", "event_content", "event_location", "hit_count", "start_date", "title", "is_operated") VALUES
//        (1, '2022-04-12 01:28:03.263549+09', NULL, 'f', NULL, 2, '2022-04-13 01:28:03.263549', '<p>asdf</p>
//
//        <p><img alt="" src="https://wiiee.s3.ap-northeast-2.amazonaws.com/dev/editor/0bb2887d-a10e-4119-b987-510a1e1dd888/614751a4-ac22-4c77-839b-8cba4de26a1d_4010880.gif" style="height:200px; width:200px" /></p>
//
//        <p>asdf</p>
//        ', 'ROTATION_BANNER1', 2, '2022-04-12 01:28:03.263549', '이벤트1', 't');
        EventForm eventForm = eventService.findByIdForForm(eventId);
        log.debug(String.valueOf("eventForm = " + eventForm));

        model.addAttribute("eventForm", eventForm);
        model.addAttribute("eventLocations", EventLocation.values());

        return "event/event_detail";
    }

    @GetMapping("/regForm")
    public String getEventRegPage(Model model) {
        log.debug("getEventRegPage()");

        EventForm eventForm = new EventForm();
        model.addAttribute("eventForm", eventForm);
        model.addAttribute("eventLocations", EventLocation.values());
        return "event/event_detail";
    }

    @PostMapping("/save")
    public String saveEvent(EventForm eventForm, @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveEvent()");
        Long id = eventForm.getId();
        log.debug(String.valueOf("id = " + id));

        if (id != null) {
            eventService.updateEvent(eventForm);
        } else {
            eventService.saveEvent(eventForm);
        }
        return "redirect:/admin/event/list";
    }

}
