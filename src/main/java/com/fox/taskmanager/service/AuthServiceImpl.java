package com.fox.taskmanager.service;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.LoginRequest;
import com.fox.taskmanager.dto.auth.RegisterRequest;
import com.fox.taskmanager.exception.AuthException;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import com.fox.taskmanager.support.AppTime;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CookieService cookieService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserProfileRepository userProfileRepository;

    public AuthServiceImpl(
            CookieService cookieService,
            JwtTokenService jwtTokenService,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            UserProfileRepository userProfileRepository) {
        this.cookieService = cookieService;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional
    public AuthResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String login = normalizeLogin(request.getLogin());

        UserProfile userProfile = userProfileRepository.findByLogin(login)
                .orElseThrow(() -> new AuthException(AppConstants.Auth.LOGIN_FAILED_MESSAGE));

        if (!passwordEncoder.matches(request.getPassword(), userProfile.getPasswordHash())) {
            throw new AuthException(AppConstants.Auth.LOGIN_FAILED_MESSAGE);
        }

        validateUserAccess(userProfile);
        updateLoginAudit(userProfile);

        issueTokens(userProfile, httpRequest, httpResponse);

        return new AuthResponse(
                AppConstants.Auth.LOGIN_SUCCESS_MESSAGE,
                AppConstants.Route.NOTE_LIST);
    }

    @Override
    @Transactional
    public AuthResponse register(
            RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String login = normalizeLogin(request.getLogin());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException(AppConstants.Auth.PASSWORD_MISMATCH_MESSAGE);
        }

        if (userProfileRepository.existsByLogin(login)) {
            throw new AuthException(AppConstants.Auth.USER_EXISTS_MESSAGE);
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(login);
        userProfile.setEmail(emptyToNull(request.getEmail()));
        userProfile.setDisplayName(login);
        userProfile.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userProfile.setLanguageCode(AppConstants.Auth.DEFAULT_LANGUAGE_CODE);
        userProfile.setRole(UserRole.USER);
        userProfile.setAuthProvider(AuthProvider.WEB);
        userProfile.setEnabled(true);
        userProfile.setAccountLocked(false);
        userProfile.setOnline(false);

        UserProfile savedUserProfile = userProfileRepository.save(userProfile);

        updateLoginAudit(savedUserProfile);
        issueTokens(savedUserProfile, httpRequest, httpResponse);

        return new AuthResponse(
                AppConstants.Auth.REGISTER_SUCCESS_MESSAGE,
                AppConstants.Route.NOTE_LIST);
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieService.clearAuthCookies(response);
    }

    private void issueTokens(
            UserProfile userProfile,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String accessToken = jwtTokenService.createAccessToken(userProfile);
        String refreshToken = jwtTokenService.createRefreshToken(userProfile);

        refreshTokenService.saveRefreshToken(
                refreshToken,
                userProfile,
                httpRequest.getHeader("User-Agent"),
                httpRequest.getRemoteAddr());

        cookieService.addAccessTokenCookie(httpResponse, accessToken);
        cookieService.addRefreshTokenCookie(httpResponse, refreshToken);
    }

    private void validateUserAccess(UserProfile userProfile) {
        if (!userProfile.isEnabled()) {
            throw new AuthException(AppConstants.Auth.ACCOUNT_DISABLED_MESSAGE);
        }

        if (userProfile.isAccountLocked()) {
            throw new AuthException(AppConstants.Auth.ACCOUNT_LOCKED_MESSAGE);
        }
    }

    private void updateLoginAudit(UserProfile userProfile) {
        LocalDateTime now = AppTime.nowUtc();

        if (userProfile.getFirstLoginAt() == null) {
            userProfile.setFirstLoginAt(now);
        }

        userProfile.setLastLoginAt(now);
        userProfile.setWebLastLoginAt(now);
        userProfile.setLastSeenAt(now);
        userProfile.setOnline(true);

        userProfileRepository.save(userProfile);
    }

    private String normalizeLogin(String login) {
        if (login == null) {
            return "";
        }

        return login.trim().toLowerCase();
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
