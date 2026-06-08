package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping(AppConstants.Route.LOGIN)
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping(AppConstants.Route.ROOT)
    public String rootPage() {
        return "redirect:" + AppConstants.Route.NOTE_LIST;
    }

    @GetMapping(AppConstants.Route.REGISTER)
    public String registerPage() {
        return "auth/register";
    }
}
