package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.service.AdminUserService;
import com.wiiee.server.admin.service.CompanyService;
import com.wiiee.server.admin.service.UserService;
import com.wiiee.server.common.domain.admin.AdminUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    @GetMapping({"/", ""})
    public String loginForm(HttpServletRequest request, Model model){

        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (request.getQueryString() != null && request.getQueryString().contains("error")) {
            errorMessage = "로그인에 실패하였습니다.";
        }
        model.addAttribute("errorMessage", errorMessage);

        return "common/loginForm";
    }

    @GetMapping({"/test_save_admin"})
    public void test_save_admin(){
        Optional<AdminUser> adminUser = adminUserService.saveAdminUser();
        userService.saveUser();
//        companyService.saveTestCompany(adminUser.get());
    }
}
