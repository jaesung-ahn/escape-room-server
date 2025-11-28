package com.wiiee.server.admin.controller;

import com.wiiee.server.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(path="/admin/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public String getUserListPage(Model model) {
        log.debug("call getUserListPage()");

        model.addAttribute("user_list", userService.findAllForForm());

        return "user/user_list";
    }
}
