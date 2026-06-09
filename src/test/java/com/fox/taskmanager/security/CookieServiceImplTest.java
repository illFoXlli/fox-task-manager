package com.fox.taskmanager.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.CookieProperties;
import com.fox.taskmanager.config.JwtProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

class CookieServiceImplTest {

    @Test
    void clearAuthCookiesExpiresAccessAndRefreshCookies() {
        CookieService cookieService = new CookieServiceImpl(
                createCookieProperties(),
                createJwtProperties());
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieService.clearAuthCookies(response);

        List<String> headers = response.getHeaders("Set-Cookie");

        assertThat(headers).hasSize(2);
        assertThat(headers.get(0))
                .contains(AppConstants.Cookie.ACCESS_TOKEN_NAME + "=")
                .contains("Max-Age=0")
                .contains("Path=" + AppConstants.Cookie.PATH)
                .contains("HttpOnly")
                .contains("SameSite=" + AppConstants.Cookie.SAME_SITE);
        assertThat(headers.get(1))
                .contains(AppConstants.Cookie.REFRESH_TOKEN_NAME + "=")
                .contains("Max-Age=0")
                .contains("Path=" + AppConstants.Cookie.PATH)
                .contains("HttpOnly")
                .contains("SameSite=" + AppConstants.Cookie.SAME_SITE);
    }

    @Test
    void addDeviceIdCookieKeepsDeviceAfterBrowserSession() {
        CookieService cookieService = new CookieServiceImpl(
                createCookieProperties(),
                createJwtProperties());
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieService.addDeviceIdCookie(response, "device-id");

        assertThat(response.getHeader("Set-Cookie"))
                .contains(AppConstants.Cookie.DEVICE_ID_NAME + "=device-id")
                .contains("Max-Age=" + AppConstants.Cookie.DEVICE_ID_MAX_AGE_DAYS * 24 * 60 * 60)
                .contains("Path=" + AppConstants.Cookie.PATH)
                .contains("HttpOnly")
                .contains("SameSite=" + AppConstants.Cookie.SAME_SITE);
    }

    private CookieProperties createCookieProperties() {
        CookieProperties properties = new CookieProperties();

        properties.setSecure(false);

        return properties;
    }

    private JwtProperties createJwtProperties() {
        JwtProperties properties = new JwtProperties();

        properties.setAccessExpirationMinutes(15);
        properties.setRefreshExpirationDays(30);

        return properties;
    }
}
