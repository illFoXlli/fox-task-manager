package com.fox.taskmanager.service;

import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.LoginRequest;
import com.fox.taskmanager.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    AuthResponse register(
            RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
