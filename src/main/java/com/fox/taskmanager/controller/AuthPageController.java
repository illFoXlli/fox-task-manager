package com.fox.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String rootPage() {
        return "redirect:/note/list";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }
}
