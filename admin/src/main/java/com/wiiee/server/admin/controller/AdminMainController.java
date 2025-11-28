package com.wiiee.server.admin.controller;

import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(path="/admin")
public class AdminMainController {

    @GetMapping({"/", "", "/main"})
    public String mainForm() {

        return "main/index";
    }

}