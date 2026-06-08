package com.fox.taskmanager.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceImplTest {

    @Test
    void logoutClearsAuthCookies() {
        CookieService cookieService = mock(CookieService.class);
        AuthService authService = new AuthServiceImpl(
                cookieService,
                mock(JwtTokenService.class),
                mock(PasswordEncoder.class),
                mock(RefreshTokenService.class),
                mock(UserProfileRepository.class));
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.logout(response);

        verify(cookieService).clearAuthCookies(response);
    }
}
