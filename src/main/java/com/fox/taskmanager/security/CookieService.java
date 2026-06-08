package com.fox.taskmanager.security;

import com.fox.taskmanager.config.AppConstants;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {

    String ACCESS_TOKEN_COOKIE = AppConstants.Cookie.ACCESS_TOKEN_NAME;

    String REFRESH_TOKEN_COOKIE = AppConstants.Cookie.REFRESH_TOKEN_NAME;

    void addAccessTokenCookie(HttpServletResponse response, String token);

    void addRefreshTokenCookie(HttpServletResponse response, String token);

    void clearAuthCookies(HttpServletResponse response);
}
