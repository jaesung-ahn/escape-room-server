package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.form.CompanyForm;
import com.wiiee.server.admin.form.content.ContentForm;
import com.wiiee.server.admin.service.CompanyService;
import com.wiiee.server.admin.service.ContentService;
import com.wiiee.server.admin.service.ImageService;
import com.wiiee.server.admin.util.S3Uploader;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.Bank;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Slf4j
@Controller
@RequestMapping(path="/admin/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    S3Uploader s3Uploader;

    @Autowired
    ImageService imageService;

    @Autowired
    ContentService contentService;

    @GetMapping("/list")
    public String getCompanyListPage(Model model) {
        log.debug("Get company list page");

        model.addAttribute("company_list", companyService.findAll());

        return "company/company_list";
    }

    @GetMapping("/regForm")
    public String getCompanyRegPage(Model model) {
        log.debug("Get company registration page");

        CompanyForm companyForm = new CompanyForm();
        model.addAttribute("companyForm",  companyForm);
        model.addAttribute("companyBanks", Bank.values());
        Iterator<City> seoulCities = City.seoulValueOf();
        model.addAttribute("seoulCities", seoulCities);
        companyForm.setOperated(true);

        return "company/company_detail";
    }

    @GetMapping("/detail")
    public String getCompanyDetailPage(@RequestParam(value="companyId") Long companyId,
                                       Model model) {
        log.debug("CompanyController.getCompanyDetailPage");

        Optional<Company> optionalCompany = companyService.findById(companyId);

        Company company = optionalCompany.get();

        List<Long> imageIds = company.getBasicInfo().getImageIds();
        String companyImgUrl = null;
        Long imageId = 0L;
        if (imageIds.size() > 0) {
            companyImgUrl = imageService.getImageById(imageIds.get(0)).getUrl();
            imageId = imageIds.get(0);
        }

        Long registrationImageId = company.getBusinessInfo().getRegistrationImageId();

        String registrationImageUrl = null;
        if (registrationImageId != null && registrationImageId > 0) {
            registrationImageUrl = imageService.getImageById(registrationImageId).getUrl();
        }

        CompanyForm companyForm = new CompanyForm();
        companyForm.setId(company.getId());
        companyForm.setName(company.getBasicInfo().getName());
        companyForm.setImageUrl(companyImgUrl);
        companyForm.setImageId(imageId);
        companyForm.setBusinessNumber(company.getBusinessInfo().getBusinessNumber());
        companyForm.setBusinessRegImageUrl(registrationImageUrl);
        companyForm.setBusinessRegImageId(registrationImageId);
        companyForm.setAddress(company.getBasicInfo().getAddress());
        companyForm.setDetailAddress(company.getBasicInfo().getDetailAddress());
        companyForm.setCompanyCityCode(company.getBasicInfo().getCity().getCode());

        List<Integer> businessDayCodes = company.getBasicInfo().getBusinessDayCodes();
        companyForm.setMon(businessDayCodes.contains(2));
        companyForm.setTue(businessDayCodes.contains(3));
        companyForm.setWed(businessDayCodes.contains(4));
        companyForm.setThu(businessDayCodes.contains(5));
        companyForm.setFri(businessDayCodes.contains(6));
        companyForm.setSat(businessDayCodes.contains(7));
        companyForm.setSun(businessDayCodes.contains(1));
        companyForm.setAlwaysOperated(company.getBasicInfo().getIsAlwaysOperated());

        companyForm.setCompanyNumber(company.getBasicInfo().getContact());
        companyForm.setHomepageUrl(company.getBasicInfo().getUrl());
        companyForm.setNotice(company.getBasicInfo().getNotice());
        companyForm.setRepresentativeName(company.getBusinessInfo().getRepresentativeName());
        companyForm.setRepContractNumber(company.getBusinessInfo().getRepContractNumber());
        companyForm.setChargeContractNumber(company.getBusinessInfo().getChargeContractNumber());
        companyForm.setCompanyBankCode(company.getBusinessInfo().getBank().getCode());
        companyForm.setSettlementAccount(company.getBusinessInfo().getAccount());

        companyForm.setOperated(company.getBasicInfo().getIsOperated());

        companyForm.setCreatedAt(company.getCreatedAt());

        Iterator<City> seoulCities = City.seoulValueOf();

        model.addAttribute("companyForm",  companyForm);
        model.addAttribute("companyBanks", Bank.values());
        model.addAttribute("seoulCities", seoulCities);
        ContentForm contentForm = new ContentForm();
        contentForm.setCompanyId(companyForm.getId());
        model.addAttribute("contents", contentService.findAllContentList(contentForm));

        return "company/company_detail";
    }

    @PostMapping("/save")
    public String saveCompany(CompanyForm companyForm, Model model, @AuthenticationPrincipal AdminUser adminUser) {
        log.debug("call saveCompany()");

        Long id = companyForm.getId();

        List<Long> imageIds = new ArrayList<>();
        List<Integer> businessDayCodes = new ArrayList<>();

        if (companyForm.getImageId() != null && companyForm.getImageId() > 0) {
            imageIds.add(companyForm.getImageId());
        }

        if (companyForm.isMon()) {
            businessDayCodes.add(2);
        }
        if (companyForm.isTue()) {
            businessDayCodes.add(3);
        }
        if (companyForm.isWed()) {
            businessDayCodes.add(4);
        }
        if (companyForm.isThu()) {
            businessDayCodes.add(5);
        }
        if (companyForm.isFri()) {
            businessDayCodes.add(6);
        }
        if (companyForm.isSat()) {
            businessDayCodes.add(7);
        }
        if (companyForm.isSun()) {
            businessDayCodes.add(1);
        }


        CompanyBasicInfo companyBasicInfo = new CompanyBasicInfo(
                companyForm.getName(),
                State.SEOUL,
                City.valueOf(companyForm.getCompanyCityCode()),
                companyForm.getAddress(),
                companyForm.getDetailAddress(),
                companyForm.getNotice(),
                companyForm.getCompanyNumber(),
                companyForm.getHomepageUrl(),
                companyForm.isOperated(),
                businessDayCodes,
                companyForm.isAlwaysOperated(),
                imageIds
        );

        CompanyBusinessInfo companyBusinessInfo = new CompanyBusinessInfo(
                companyForm.getBusinessRegImageId(),
                companyForm.getBusinessNumber(),
                companyForm.getRepresentativeName(),
                companyForm.getRepContractNumber(),
                companyForm.getChargeContractNumber(),
                Bank.valueOf(companyForm.getCompanyBankCode()),
                companyForm.getSettlementAccount()
        );

        if (id != null) {
            Optional<Company> company = companyService.findById(id);

            company.get().updateCompany(companyBasicInfo, companyBusinessInfo);
            companyService.updateCompany(company);

        } else {

            Company company = new Company(adminUser, companyBasicInfo, companyBusinessInfo);

            companyService.saveCompany(company);
        }

        return "redirect:/admin/company/list";
    }

    @ResponseBody
    @PostMapping("/images_upload")
    public HashMap<String, String> upload(@RequestParam("file") MultipartFile multipartFile,
                                          @RequestParam("group") String group, @RequestParam("detail_id") String detailId
                                          ) throws IOException {

        Image image = imageService.uploadOne(multipartFile, "dev" + "/" + group + "/" + detailId);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("image_id", String.valueOf(image.getId()));
        map.put("return_s3_url", image.getUrl());
        map.put("status", "0000");

        log.debug("Image upload response: {}", map);
        return map;
    }

    @PostMapping("/editor_image_upload")
    public void editor_image_upload(MultipartFile upload, HttpServletResponse res,
                                                       HttpServletRequest req) throws IOException {

        OutputStream out = null;
        PrintWriter printWriter = null;

        res.setCharacterEncoding("utf-8");
        res.setContentType("text/html;charset=utf-8");

        try{

            UUID uuid = UUID.randomUUID();

            // 이미지 저장
            Image image = imageService.uploadOne(upload, "dev" + "/" + "editor" + "/" + uuid);

            // ckEditor 로 전송
            printWriter = res.getWriter();
            String callback = req.getParameter("CKEditorFuncNum");

            printWriter.println("<script type='text/javascript'>"
                    + "window.parent.CKEDITOR.tools.callFunction("
                    + callback+",'"+ image.getUrl() +"','이미지를 업로드하였습니다.')"
                    +"</script>");

            printWriter.flush();


        } catch (IOException e) {
            log.error("Error occurred", e);
        }
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }


}
