package com.fox.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.LoginRequest;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.security.ClientIpResolver;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceImplTest {

    private static final String ACCESS_TOKEN = "access-token";
    private static final String DEVICE_ID = "device-id";
    private static final String REFRESH_TOKEN = "refresh-token";

    private ClientIpResolver clientIpResolver;
    private CookieService cookieService;
    private JwtTokenService jwtTokenService;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenService refreshTokenService;
    private UserProfileRepository userProfileRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        clientIpResolver = mock(ClientIpResolver.class);
        cookieService = mock(CookieService.class);
        jwtTokenService = mock(JwtTokenService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokenService = mock(RefreshTokenService.class);
        userProfileRepository = mock(UserProfileRepository.class);
        authService = new AuthServiceImpl(
                clientIpResolver,
                cookieService,
                jwtTokenService,
                passwordEncoder,
                refreshTokenService,
                userProfileRepository);
    }

    @Test
    void loginRedirectsToNoteView() {
        LoginRequest request = new LoginRequest();
        request.setLogin(" fox ");
        request.setPassword("password");
        UserProfile userProfile = createEnabledUserProfile("fox");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        when(userProfileRepository.findByLogin("fox")).thenReturn(Optional.of(userProfile));
        when(passwordEncoder.matches("password", userProfile.getPasswordHash())).thenReturn(true);
        when(jwtTokenService.createAccessToken(userProfile)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenService.createRefreshToken(userProfile)).thenReturn(REFRESH_TOKEN);
        when(clientIpResolver.resolve(httpRequest)).thenReturn("127.0.0.1");

        AuthResponse response = authService.login(request, httpRequest, httpResponse);

        assertThat(response.getRedirectUrl()).isEqualTo(AppConstants.Route.NOTE_VIEW);
    }

    @Test
    void logoutClearsAuthCookies() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        authService.logout(request, response);

        verify(cookieService).clearAuthCookies(response);
    }

    @Test
    void logoutRevokesCurrentRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getCookies())
                .thenReturn(new Cookie[] {
                        new Cookie(CookieService.REFRESH_TOKEN_COOKIE, REFRESH_TOKEN)
                });

        authService.logout(request, response);

        verify(refreshTokenService).revokeRefreshToken(REFRESH_TOKEN);
    }

    private UserProfile createEnabledUserProfile(String login) {
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(login);
        userProfile.setPasswordHash("password-hash");
        userProfile.setDisplayName(login);
        userProfile.setLanguageCode(AppConstants.Auth.DEFAULT_LANGUAGE_CODE);
        userProfile.setRole(UserRole.USER);
        userProfile.setAuthProvider(AuthProvider.WEB);
        userProfile.setEnabled(true);
        userProfile.setAccountLocked(false);

        return userProfile;
    }

}
