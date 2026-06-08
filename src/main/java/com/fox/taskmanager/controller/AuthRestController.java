package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.LoginRequest;
import com.fox.taskmanager.dto.auth.RegisterRequest;
import com.fox.taskmanager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return ResponseEntity.ok(
                authService.login(request, httpRequest, httpResponse));
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return ResponseEntity.ok(
                authService.register(request, httpRequest, httpResponse));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        authService.logout(response);

        return ResponseEntity.ok(
                new AuthResponse(
                        AppConstants.Auth.LOGOUT_SUCCESS_MESSAGE,
                        AppConstants.Route.LOGIN));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AuthResponse> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(exception.getMessage(), null));
    }
}
