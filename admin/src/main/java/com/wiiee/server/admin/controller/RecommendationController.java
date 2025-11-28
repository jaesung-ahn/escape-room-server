package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.RecommendationForm;
import com.wiiee.server.admin.service.ContentService;
import com.wiiee.server.admin.service.RecommendationService;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.user.UserGenderType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path="/admin/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ContentService contentService;

    @GetMapping("/list")
    public String getRecommendationListPage(Model model) {
        log.debug("call getRecommendationListPage()");

        model.addAttribute("recommendation_list", recommendationService.findAll());

        return "recommendation/recommendation_list";
    }

    @GetMapping("/regForm")
    public String getRecommendationRegPage(Model model) {
        log.debug("call getRecommendationRegPage()");

        RecommendationForm recommendationForm = RecommendationForm.recommendationForm();
        model.addAttribute("recommendationForm", recommendationForm);
        model.addAttribute("seoulCities", City.seoulValueOf());
        model.addAttribute("userGenderTypes", UserGenderType.values());
        model.addAttribute("ageGroupInfos", AgeGroup.values());
        model.addAttribute("contents", contentService.findAllSimpleContentForm());

        return "recommendation/recommendation_detail";
    }

    @GetMapping({"/detail"})
    public String getRecommendationDetailPage(@RequestParam(value="recommendationId") Long recommendationId,
                                         Model model) {
        RecommendationForm recommendationForm = recommendationService.findByIdForForm(recommendationId);

        model.addAttribute("recommendationForm", recommendationForm);
        model.addAttribute("seoulCities", City.seoulValueOf());
        model.addAttribute("userGenderTypes", UserGenderType.values());
        model.addAttribute("ageGroupInfos", AgeGroup.values());
        model.addAttribute("contents", contentService.findAllSimpleContentForm());

        return "recommendation/recommendation_detail";
    }

    @ResponseBody
    @PostMapping("/save")
    public HashMap<String, String> saveRecommendation(HttpServletRequest request, Model model,
                                               @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveRecommendation()");
        HashMap<String, String> map = new HashMap<String, String>();

        RecommendationForm recommendationForm = RecommendationForm.recommendationForm();

        String[] recommendationForms = request.getParameterValues("recommendationForm");
        String id = request.getParameter("id");
        String[] contentList = request.getParameterValues("contentList");

        log.debug(String.valueOf("recommendationForm = " + recommendationForms));
        log.debug(String.valueOf("id = " + id));
        log.debug(String.valueOf("contentList = " + contentList));

        for (String form : recommendationForms) {
            log.debug(String.valueOf("recommendationForm : " + form));
            JSONArray jsonArray = new JSONArray(form);
            log.debug(String.valueOf("jsonArray = " + jsonArray));
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject obj = jsonArray.getJSONObject(j);
                String name = obj.getString("name");
                String value = obj.getString("value");
                log.debug(String.valueOf("name = " + name));
                log.debug(String.valueOf("value = " + value));
                if (name.equals("categoryName") && value != null) {
                    if (id != null && id.length() > 0) {
                        recommendationForm = RecommendationForm.recommendationForm(Long.valueOf(id), value);
                    }
                    else {
                        recommendationForm = RecommendationForm.recommendationForm(null, value);
                    }
                } else if (name.equals("cityCode") && value.length() > 0) {
                    recommendationForm.setCityCode(Integer.parseInt(value));
                } else if (name.equals("userGenderTypeCode") && value.length() > 0) {
                    recommendationForm.setUserGenderTypeCode(Integer.parseInt(value));
                } else if (name.equals("ageGroupInfoCode") && value.length() > 0) {
                    recommendationForm.setAgeGroupInfoCode(Integer.parseInt(value));
                }
            }
        }
        List<RecommendationForm.ContentForm> contentForms = new ArrayList<>();
        if (contentList != null) {
            for (String contentValue : contentList) {
                log.debug(String.valueOf("contentList : " + contentValue));
                contentForms.add(new RecommendationForm.ContentForm(Long.parseLong(contentValue), null));
            }
            recommendationForm.setContentForms(contentForms);
        }


        if (id != null && id.length() > 0) {
            recommendationService.updateRDT(recommendationForm);
        }
        else {
            recommendationService.saveRDT(recommendationForm);
        }


        map.put("status", ResponseConst.SUCCESS);
        return map;
    }
}
