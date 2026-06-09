package com.fox.taskmanager.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private static final String LOGIN = "fox";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void restoresAccessCookieFromValidRefreshToken() throws ServletException, IOException {
        final CookieService cookieService = mock(CookieService.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
        final UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setCookies(new Cookie(CookieService.REFRESH_TOKEN_COOKIE, REFRESH_TOKEN));

        UserProfile userProfile = createUserProfile();

        when(jwtTokenService.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenService.extractLogin(REFRESH_TOKEN)).thenReturn(LOGIN);
        when(jwtTokenService.createAccessToken(userProfile)).thenReturn(NEW_ACCESS_TOKEN);
        when(refreshTokenService.getValidRefreshToken(REFRESH_TOKEN))
                .thenReturn(new RefreshToken());
        when(userProfileRepository.findByLogin(LOGIN)).thenReturn(Optional.of(userProfile));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                cookieService,
                jwtTokenService,
                refreshTokenService,
                userProfileRepository);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo(LOGIN);
        verify(cookieService).addAccessTokenCookie(response, NEW_ACCESS_TOKEN);
    }

    @Test
    void clearsAuthCookiesWhenRefreshTokenIsInvalid() throws ServletException, IOException {
        final CookieService cookieService = mock(CookieService.class);
        final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
        final UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setCookies(new Cookie(CookieService.REFRESH_TOKEN_COOKIE, REFRESH_TOKEN));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                cookieService,
                jwtTokenService,
                refreshTokenService,
                userProfileRepository);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        verify(cookieService).clearAuthCookies(response);
        assertThat(response.getHeader(HttpHeaders.SET_COOKIE)).isNull();
    }

    private UserProfile createUserProfile() {
        UserProfile userProfile = new UserProfile();

        userProfile.setLogin(LOGIN);
        userProfile.setRole(UserRole.USER);
        userProfile.setEnabled(true);
        userProfile.setAccountLocked(false);

        return userProfile;
    }
}
