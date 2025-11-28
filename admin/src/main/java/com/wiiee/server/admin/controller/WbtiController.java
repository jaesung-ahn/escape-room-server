package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.WbtiDetailForm;
import com.wiiee.server.admin.service.WbtiService;
import com.wiiee.server.common.domain.admin.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(path="/admin/wbti")
@RequiredArgsConstructor
public class WbtiController {

    private final WbtiService wbtiService;

    @GetMapping("/list")
    public String getWbtiListPage(Model model) {
        log.debug("call getWbtiListPage()");

        model.addAttribute("wbti_list", wbtiService.findAllForForm());

        return "wbti/wbti_list";
    }

    @GetMapping("/detail")
    public String getWbtiDetailPage(@RequestParam(value="wbtiId") Long wbtiId,
                                      Model model) {
        WbtiDetailForm wbtiForm = wbtiService.findByIdForForm(wbtiId);

        log.debug(String.valueOf("wbtiForm = " + wbtiForm));

        List<Long> wbtiPartnerList = wbtiForm.getWbtiPartnerForms().stream().map(wbti -> wbti.getWbtiId())
                .collect(Collectors.toList());

        model.addAttribute("wbtiForm",  wbtiForm);
        model.addAttribute("wbtiPartnerList", wbtiPartnerList);
        model.addAttribute("wbtiList",  wbtiService.findAllForForm());
        return "wbti/wbti_detail";
    }

    @ResponseBody
    @PostMapping("/save")
    public HashMap<String, String> saveWbti(HttpServletRequest request,
                                            WbtiDetailForm wbtiDetailForm,
                                                      @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveWbti()");
        HashMap<String, String> map = new HashMap<String, String>();
        log.debug(String.valueOf("wbtiDetailForm = " + wbtiDetailForm));
        if (wbtiDetailForm.getId() != null && wbtiDetailForm.getId() > 0) {
            wbtiService.updateWbti(wbtiDetailForm);
        }
        map.put("status", ResponseConst.SUCCESS);
        return map;
    }
}
