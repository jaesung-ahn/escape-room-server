package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.ReviewDetailForm;
import com.wiiee.server.admin.form.ReviewListForm;
import com.wiiee.server.admin.service.ReviewService;
import com.wiiee.server.common.domain.admin.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping(path="/admin/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/list")
    public String getReviewListPage(Model model) {
        log.debug("call ReviewController.getReviewListPage()");

        model.addAttribute("review_list", reviewService.findAllForForm());

        return "review/review_list";
    }

    @GetMapping({"/detail"})
    public String getReviewDetailPage(@RequestParam(value="reviewId") Long reviewId,
                                      Model model) {
        ReviewDetailForm reviewForm = reviewService.findByIdForForm(reviewId);

        log.debug(String.valueOf("reviewForm = " + reviewForm));

        model.addAttribute("reviewForm",  reviewForm);
        return "review/review_detail";
    }

    @ResponseBody
    @PostMapping({"/save"})
    public HashMap<String, String> saveReview(@Validated ReviewListForm reviewListForm,
                                              @AuthenticationPrincipal AdminUser adminUser) {
        HashMap<String, String> map = new HashMap<String, String>();

        reviewService.updateReview(reviewListForm);

        map.put("status", ResponseConst.SUCCESS);
        return map;
    }
}
