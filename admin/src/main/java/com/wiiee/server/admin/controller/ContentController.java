package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.config.ResponseConst;
import com.wiiee.server.admin.form.content.ContentForm;
import com.wiiee.server.admin.service.CompanyService;
import com.wiiee.server.admin.service.ContentService;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.content.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequestMapping(path="/admin/content")
public class ContentController {

    @Autowired
    ContentService contentService;

    @Autowired
    CompanyService companyService;

    @GetMapping("/list")
    public String getContentListPage(Model model, @RequestParam(value="companyId", defaultValue = "0") Long companyId) {
        log.debug("call ContentController.getContentListPage()");

        ContentForm contentForm = new ContentForm();
        if (!Objects.equals(companyId, "0")) {
            contentForm.setCompanyId(companyId);
        }
        log.debug("contentForm: {}, companyId: {}", contentForm, contentForm.getCompanyId());

        model.addAttribute("content_list", contentService.findAllContentList(contentForm));

        return "content/content_list";
    }

    @GetMapping("/regForm")
    public String getContentRegPage(Model model, HttpServletRequest request, @RequestParam(value="companyId") Long companyId) {
        log.debug("ContentController.getContentRegPage()");

        if (companyId == null || companyId < 1) {
            String referer = request.getHeader("Referer");
            return "redirect:"+ referer;
        }

        ContentForm contentForm = new ContentForm();

        Company company = companyService.findById(companyId).get();

        contentForm.setCompanyId(companyId);
        contentForm.setCompanyName(company.getBasicInfo().getName());

        List<ContentForm.ContentImageForm> contentImages = new ArrayList<>();
        contentImages.add(new ContentForm.ContentImageForm(null, -1L, true));
        contentForm.setContentImages(contentImages);
        model.addAttribute("contentForm",  contentForm);

        model.addAttribute("contentImages", contentForm.getContentImages());

        model.addAttribute("genres", Genre.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("activityLevels", ActivityLevel.values());
        model.addAttribute("escapeTypes", EscapeType.values());

        return "content/content_detail";
    }

    @GetMapping("/detail")
    public String getContentDetailPage(@RequestParam(value="contentId") Long contentId,
                                       Model model) {
        log.debug("ContentController.getContentDetailPage()");

        ContentForm contentForm = contentService.findById(contentId);

        model.addAttribute("contentForm", contentForm);

        if (contentForm.getContentImages() != null && contentForm.getContentImages().size() > 0) {
            model.addAttribute("contentImages", contentForm.getContentImages());
        }
        else {
            model.addAttribute("contentImages", null);
        }

        if (contentForm.getContentPrices() != null && contentForm.getContentPrices().size() > 0) {
            model.addAttribute("contentPrices", contentForm.getContentPrices());
        }
        else {
            model.addAttribute("contentPrices", null);
        }


        model.addAttribute("genres", Genre.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("activityLevels", ActivityLevel.values());
        model.addAttribute("escapeTypes", EscapeType.values());

        return "content/content_detail";
    }

    @ResponseBody
    @PostMapping("/delete_content_price")
    public HashMap<String, String> deleteContentPrice( HttpServletRequest request, Model model,
                                                       @RequestParam(value="contentPriceId") Long contentPriceId,
                                                       @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call deleteContentPrice()");
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            map.put("status", ResponseConst.SUCCESS);
            contentService.deleteContentPrice(contentPriceId);
        }catch (Exception e) {
            map.put("status", ResponseConst.DEFAULT_ERROR);
        }

        return map;
    }

    @ResponseBody
    @PostMapping("/save")
    public HashMap<String, String> saveContent( HttpServletRequest request, Model model,
                                               @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveContent()");
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            ContentForm contentForm = new ContentForm();
            String[] contentForms = request.getParameterValues("contentForm");
            String[] contentImages = request.getParameterValues("contentImages");
            String[] isRepresentives = request.getParameterValues("isRepresentives");
            boolean isDisplayNew = Boolean.parseBoolean(request.getParameter("isDisplayNew"));
            boolean isNoEscapeType = Boolean.parseBoolean(request.getParameter("isNoEscapeType"));

            contentForm.setDisplayNew(isDisplayNew);
            contentForm.setNoEscapeType(isNoEscapeType);

            List<Integer> peopleList = new ArrayList<>();
            List<Integer> priceList = new ArrayList<>();
            List<Long> priceIdList = new ArrayList<>();

            for (String form : contentForms) {
                log.debug(String.valueOf("contentForm : " + form));
                JSONArray jsonArray = new JSONArray(form);
                log.debug(String.valueOf("jsonArray = " + jsonArray));
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject obj = jsonArray.getJSONObject(j);
                    log.debug("Processing field - name: {}, value: {}", obj.getString("name"), obj.getString("value"));
                    if (obj.getString("name").equals("id")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setId(Long.valueOf(obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("companyId")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setCompanyId(Long.valueOf(obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("operated")) {
                        if (obj.getString("value") != null ) {
                            contentForm.setOperated(Boolean.parseBoolean(obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("name")) {
                        contentForm.setName((obj.getString("value")));
                    } else if (obj.getString("name").equals("newDisplayExpirationDate")) {
                        if (obj.getString("value") != null) {
                            contentForm.setNewDisplayExpirationDate((obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("genreCode")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setGenreCode(Integer.parseInt((obj.getString("value"))));
                        }

                    } else if (obj.getString("name").equals("minPeople")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setMinPeople(Integer.parseInt((obj.getString("value"))));
                        } else {
                            contentForm.setMinPeople(0);
                        }
                    } else if (obj.getString("name").equals("maxPeople")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setMaxPeople(Integer.parseInt((obj.getString("value"))));
                        } else {
                            contentForm.setMaxPeople(0);
                        }
                    } else if (obj.getString("name").equals("difficultyCode")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setDifficultyCode(Integer.parseInt((obj.getString("value"))));
                        }
                    } else if (obj.getString("name").equals("activityLevelCode")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setActivityLevelCode(Integer.parseInt((obj.getString("value"))));
                        }
                    } else if (obj.getString("name").equals("escapeTypeCode")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setEscapeTypeCode(Integer.parseInt((obj.getString("value"))));
                        }
                    } else if (obj.getString("name").equals("playTime")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            contentForm.setPlayTime(Integer.parseInt((obj.getString("value"))));
                        }
                    } else if (obj.getString("name").equals("information")) {
                        if (obj.getString("value") != null) {
                            contentForm.setInformation(obj.getString("value"));
                        }
                    }

                    // 가격 설정
                    if (obj.getString("name").equals("peopleNumber")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            peopleList.add(Integer.valueOf(obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("price")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            priceList.add(Integer.valueOf(obj.getString("value")));
                        }
                    } else if (obj.getString("name").equals("contentPriceId")) {
                        if (obj.getString("value") != null && NumberUtils.isCreatable(obj.getString("value"))) {
                            priceIdList.add(Long.valueOf(obj.getString("value")));
                        } else {
                            priceIdList.add(null);
                        }
                    }

                }

            }
            List<Long> imageIds = new ArrayList<>();
            int repIndex = 0;
            for (String isRepresentive: isRepresentives) {
                log.debug(String.valueOf("isRepresentive = " + isRepresentive));
                if (Boolean.parseBoolean(isRepresentive)) {
                    break;
                }
                else {
                    repIndex++;
                }
            }

            for (String contentImageId: contentImages) {
                log.debug(String.valueOf("contentImage = " + contentImageId));
                if (contentImageId.length() > 0 && Long.parseLong(contentImageId) > 0) {
                    imageIds.add(Long.valueOf(contentImageId));
                }
            }
            if (imageIds.size() > 0) {
                // 해당 이미지 목록에서 이미지 id 가져오기
                Long repImgId = imageIds.get(repIndex);

                // 해당 인덱스 제거 후, 이미지 id 처음으로 집어넣기
                imageIds.remove(repIndex);
                imageIds.add(0, repImgId);
            }

            log.debug(String.valueOf("peopleList = " + peopleList));
            log.debug(String.valueOf("priceList = " + priceList));

            List< ContentForm.ContentPriceForm > contentPriceForms = new ArrayList<>();
            IntStream.range(0, peopleList.size())
                    .forEach(index ->
                            contentPriceForms.add(new ContentForm.ContentPriceForm(
                                    priceIdList.get(index),
                                    peopleList.get(index),
                                    priceList.get(index)
                            ))
                    );

            log.debug(String.valueOf("contentForms = " + contentForms));
            log.debug(String.valueOf("imageIds = " + imageIds));
            log.debug(String.valueOf("contentPriceForms = " + contentPriceForms));

            //Next parse the date from the @RequestParam, specifying the TO type as
            LocalDate newDisplayExpirationDate = null;
            if (contentForm.getNewDisplayExpirationDate() != null && contentForm.getNewDisplayExpirationDate().length() > 5) {
                newDisplayExpirationDate = LocalDate.parse(contentForm.getNewDisplayExpirationDate(), DateTimeFormatter.ISO_DATE);
            }

            boolean isCuation = false;
            if (Genre.valueOf(contentForm.getGenreCode()) == Genre.HORROR) {
                isCuation = true;
            }

            ContentBasicInfo contentBasicInfo = new ContentBasicInfo(
                    contentForm.getName(),
                    Genre.valueOf(contentForm.getGenreCode()),
                    contentForm.getInformation(),
                    contentForm.getPlayTime(),
                    ActivityLevel.valueOf(contentForm.getActivityLevelCode()),
                    EscapeType.valueOf(contentForm.getEscapeTypeCode()),
                    isCuation,
                    contentForm.getMinPeople(),
                    contentForm.getMaxPeople(),
                    Difficulty.valueOf(contentForm.getDifficultyCode()),
                    imageIds,
                    contentForm.getNoEscapeType(),
                    contentForm.isDisplayNew(),
                    newDisplayExpirationDate,
                    contentForm.isOperated()
            );
//
            if (contentForm.getId() != null) {
                Content content = contentService.updateContent(contentForm.getId(), contentPriceForms);

                content.updateContent(contentBasicInfo);
                // 이렇게 호출하면 jpa no session 오류 발생
//            contentService.addContentPrice(content, contentPriceForms);
                contentService.saveContent(content);
            }
            else {
                contentService.createContent(contentForm.getCompanyId(), contentBasicInfo, contentPriceForms);

            }

            map.put("status", ResponseConst.SUCCESS);
//        return "redirect:/admin/content/list";
            return map;
        } catch (Exception e) {
            map.put("status", ResponseConst.DEFAULT_ERROR);
            return map;
        }

    }
}
