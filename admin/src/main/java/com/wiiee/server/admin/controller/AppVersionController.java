package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.AppVersionDetailForm;
import com.wiiee.server.admin.service.AppVersionService;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.appVersion.AppOs;
import com.wiiee.server.common.domain.appVersion.SelectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path="/admin/appVersion")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping("/list")
    public String getAppVersionListPage(Model model) {
        log.debug("call getAppVersionListPage()");

        model.addAttribute("app_version_list", appVersionService.findAll());

        return "app_version/app_version_list";
    }

    @GetMapping("/regForm")
    public String getAppVersionRegPage(Model model) {
        log.debug("getAppVersionRegPage()");

        model.addAttribute("appVersionForm",  new AppVersionDetailForm());
        model.addAttribute("AppOsList", AppOs.values());
        model.addAttribute("SelectionTypeList", SelectionType.values());
        return "app_version/app_version_detail";
    }

    @ResponseBody
    @PostMapping("/save")
    public HashMap<String, String> saveAppVersion(AppVersionDetailForm appVersionDetailForm, @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveAppVersion()");
        Long id = appVersionDetailForm.getId();
        log.debug(String.valueOf("id = " + id));

        if (id != null) {
            appVersionService.updateAppVersion(appVersionDetailForm);
        } else {
            appVersionService.saveAppVersion(appVersionDetailForm);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("status", ResponseConst.SUCCESS);
        return map;
    }

    @GetMapping({"/detail"})
    public String getReviewDetailPage(@RequestParam(value="appVersionId") Long appVersionId, Model model) {
        AppVersionDetailForm appVersionDetailForm = appVersionService.findByIdForForm(appVersionId);
        ArrayList<Integer> selectionCodeList = new ArrayList<>();
        selectionCodeList.add(SelectionType.REQUIRE.getCode());
        selectionCodeList.add(SelectionType.OPTION.getCode());

        model.addAttribute("appVersionForm",  appVersionDetailForm);
        model.addAttribute("AppOsList", AppOs.values());
        model.addAttribute("SelectionTypeList", SelectionType.values());
        return "app_version/app_version_detail";
    }
}
