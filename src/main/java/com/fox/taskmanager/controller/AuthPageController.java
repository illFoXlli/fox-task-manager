package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.WebRedirect;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.LoginRequest;
import com.fox.taskmanager.dto.auth.RegisterRequest;
import com.fox.taskmanager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthPageController {

    private final AuthService authService;

    public AuthPageController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(AppConstants.Route.LOGIN)
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping(AppConstants.Route.LOGIN)
    public String loginPageSubmit(
            @Valid @ModelAttribute LoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginError", AppConstants.Auth.LOGIN_FAILED_MESSAGE);
            return "auth/login";
        }

        try {
            AuthResponse authResponse = authService.login(request, httpRequest, httpResponse);

            WebRedirect.sendRelativeRedirect(httpResponse, authResponse.getRedirectUrl());
            return null;
        } catch (RuntimeException exception) {
            model.addAttribute("loginError", exception.getMessage());
            return "auth/login";
        }
    }

    @GetMapping(AppConstants.Route.LOGOUT)
    public void logoutPage(
            HttpServletRequest request,
            HttpServletResponse response) {
        authService.logout(request, response);
        WebRedirect.sendRelativeRedirect(response, AppConstants.Route.LOGIN);
    }

    @GetMapping(AppConstants.Route.ROOT)
    public void rootPage(HttpServletResponse response) {
        WebRedirect.sendRelativeRedirect(response, AppConstants.Route.NOTE_LIST);
    }

    @GetMapping(AppConstants.Route.REGISTER)
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping(AppConstants.Route.REGISTER)
    public String registerPageSubmit(
            @Valid @ModelAttribute RegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerError", AppConstants.Auth.REGISTER_FAILED_MESSAGE);
            return "auth/register";
        }

        try {
            AuthResponse authResponse = authService.register(request, httpRequest, httpResponse);

            WebRedirect.sendRelativeRedirect(httpResponse, authResponse.getRedirectUrl());
            return null;
        } catch (RuntimeException exception) {
            model.addAttribute("registerError", exception.getMessage());
            return "auth/register";
        }
    }
}
