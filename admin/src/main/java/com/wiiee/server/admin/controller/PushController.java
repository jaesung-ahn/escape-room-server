package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.PushForm;
import com.wiiee.server.admin.service.EventService;
import com.wiiee.server.admin.service.PushHistoryService;
import com.wiiee.server.admin.service.UserService;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.push.PushHistory;
import com.wiiee.server.common.domain.push.PushType;
import com.wiiee.server.common.domain.user.UserOS;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path="/admin/pushHistory")
public class PushController {

    private final EventService eventService;

    private final PushHistoryService pushHistoryService;

    private final UserService userService;

    @GetMapping({"/list"})
    public String getPushListPage(Model model) {
        model.addAttribute("push_history_list", pushHistoryService.findAllByPushHistoryListReqDTO());
        return "push/push_list";
    }

    @GetMapping({"/detail"})
    public String getPushDetailPage(@RequestParam(value="pushHistoryId") Long pushHistoryId,
                                         Model model) {
        log.debug("PushController.getPushDetailPage");

        PushForm pushForm = new PushForm();
        model.addAttribute("pushForm", pushHistoryService.findByIdForForm(pushHistoryId));
        model.addAttribute("pushTypes", PushType.values());
        model.addAttribute("events", eventService.findAllByEnableEvent());
        model.addAttribute("userOS", UserOS.values());
        return "push/push_detail";
    }

    @GetMapping("/regForm")
    public String getPushRegPage(Model model) {
        log.debug("getPushRegPage()");

        PushForm pushForm = new PushForm();
        model.addAttribute("pushForm", pushForm);
        model.addAttribute("pushTypes", PushType.values());
        model.addAttribute("events", eventService.findAllByEnableEvent());
        model.addAttribute("userOS", UserOS.values());
        model.addAttribute("countAndroidEventUser", userService.countAndroidEventUser());
        return "push/push_detail";
    }

    @ResponseBody
    @PostMapping("/save")
    public HashMap<String, String> savePushHistory(HttpServletRequest request, Model model,
                                                      PushForm pushForm,
                                                      @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("PushController.savePushHistory");
        HashMap<String, String> map = new HashMap<String, String>();

        log.debug(String.valueOf("map = " + map));
        log.debug(String.valueOf("pushForm = " + pushForm));
        if (pushForm.getPushTypeCode() == PushType.EVENT_ALL_USER.getCode()) {
            PushHistory pushHistory = pushHistoryService.savePushHistory(pushForm);
            OkHttpClient client = new OkHttpClient();

            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("eventId", pushForm.getEventId());
            requestMap.put("title", pushForm.getTitle());
            requestMap.put("pushContent", pushForm.getPushContent());
            requestMap.put("pushHistoryId", pushHistory.getId());
            String message = new JSONObject(requestMap).toJSONString();

            log.debug(String.valueOf("request message = " + message));

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            final String API_URL = "http://127.0.0.1:8082/api/push/sendEventPush/";

            Request requestObj = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .build();

            Response response = null;
            try {
                response = client.newCall(requestObj)
                        .execute();

                String responseBody = response.body() != null ? response.body().string() : null;
                log.info("Push response: {}", responseBody);
            } catch (IOException e) {
                log.error("Push request failed", e);
                throw new RuntimeException(e);
            }
        }

        map.put("status", ResponseConst.SUCCESS);
        return map;
    }
}
