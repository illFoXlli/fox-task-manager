package com.fox.taskmanager.security;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.CookieProperties;
import com.fox.taskmanager.config.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {

    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;

    public CookieServiceImpl(
            CookieProperties cookieProperties,
            JwtProperties jwtProperties) {
        this.cookieProperties = cookieProperties;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        Duration maxAge = Duration.ofMinutes(
                jwtProperties.getAccessExpirationMinutes());

        addCookie(response, ACCESS_TOKEN_COOKIE, token, maxAge);
    }

    @Override
    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Duration maxAge = Duration.ofDays(
                jwtProperties.getRefreshExpirationDays());

        addCookie(response, REFRESH_TOKEN_COOKIE, token, maxAge);
    }

    @Override
    public void addDeviceIdCookie(HttpServletResponse response, String deviceId) {
        addCookie(
                response,
                DEVICE_ID_COOKIE,
                deviceId,
                Duration.ofDays(AppConstants.Cookie.DEVICE_ID_MAX_AGE_DAYS));
    }

    @Override
    public void clearAuthCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", Duration.ZERO);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", Duration.ZERO);
    }

    private void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(AppConstants.Cookie.SAME_SITE)
                .path(AppConstants.Cookie.PATH)
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
