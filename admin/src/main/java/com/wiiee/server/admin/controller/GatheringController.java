package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.form.GatheringForm;
import com.wiiee.server.admin.service.ContentService;
import com.wiiee.server.admin.service.GatheringService;
import com.wiiee.server.admin.service.UserService;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path="/admin/gathering")
public class GatheringController {

    @Autowired
    GatheringService gatheringService;

    @Autowired
    ContentService contentService;
    @Autowired
    UserService userService;

    @GetMapping("/list")
    public String getGatheringListPage(Model model) {
        log.debug("call getGatheringListPage()");

        model.addAttribute("gathering_list", gatheringService.findAllForForm());

        return "gathering/gathering_list";
    }

    @GetMapping({"/detail"})
    public String getGatheringDetailPage(@RequestParam(value="gatheringId") Long gatheringId,
                                         Model model) {

        GatheringForm gatheringForm = gatheringService.findByIdForForm(gatheringId);

        model.addAttribute("gatheringForm", gatheringForm);

        model.addAttribute("seoulCities", City.seoulValueOf());
        List<Integer> ageGroupList = gatheringForm.getAgeGroups();

        List<AgeGroup> ageGroups = new ArrayList<>();

        if (ageGroupList != null) {
            for (Integer ageGroupCode : ageGroupList) {
                ageGroups.add(AgeGroup.valueOf(ageGroupCode));
            }
        }


        model.addAttribute("ageGroups", ageGroups);
        model.addAttribute("gatheringStatusValues", GatheringStatus.values());

        return "gathering/gathering_detail";
    }

    @ResponseBody
    @GetMapping({"/test_save_gathering"})
    public String testSaveGathering(Model model) {

//        gatheringService.saveTestGathering();
        return "success";
    }
}
