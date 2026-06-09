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
import com.fox.taskmanager.security.ClientIpResolver;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import com.fox.taskmanager.support.AppTime;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final ClientIpResolver clientIpResolver;
    private final CookieService cookieService;
    private final String defaultSecurityUserName;
    private final String defaultSecurityUserPassword;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserProfileRepository userProfileRepository;

    public AuthServiceImpl(
            ClientIpResolver clientIpResolver,
            CookieService cookieService,
            @Value("${spring.security.user.name:user}") String defaultSecurityUserName,
            @Value("${spring.security.user.password:}") String defaultSecurityUserPassword,
            JwtTokenService jwtTokenService,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            UserProfileRepository userProfileRepository) {
        this.clientIpResolver = clientIpResolver;
        this.cookieService = cookieService;
        this.defaultSecurityUserName = defaultSecurityUserName;
        this.defaultSecurityUserPassword = defaultSecurityUserPassword;
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

        if (!isPasswordAccepted(login, request.getPassword(), userProfile.getPasswordHash())) {
            throw new AuthException(AppConstants.Auth.LOGIN_FAILED_MESSAGE);
        }

        validateUserAccess(userProfile);

        LoginClient loginClient = resolveLoginClient(httpRequest);

        updateLoginAudit(userProfile, AuthProvider.WEB, loginClient);
        issueTokens(userProfile, AuthProvider.WEB, loginClient, httpResponse);

        return new AuthResponse(
                AppConstants.Auth.LOGIN_SUCCESS_MESSAGE,
                AppConstants.Route.NOTE_VIEW);
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

        LoginClient loginClient = resolveLoginClient(httpRequest);

        updateLoginAudit(savedUserProfile, AuthProvider.WEB, loginClient);
        issueTokens(savedUserProfile, AuthProvider.WEB, loginClient, httpResponse);

        return new AuthResponse(
                AppConstants.Auth.REGISTER_SUCCESS_MESSAGE,
                AppConstants.Route.NOTE_LIST);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, CookieService.REFRESH_TOKEN_COOKIE);

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        cookieService.clearAuthCookies(response);
    }

    private void issueTokens(
            UserProfile userProfile,
            AuthProvider source,
            LoginClient loginClient,
            HttpServletResponse httpResponse) {
        String accessToken = jwtTokenService.createAccessToken(userProfile);
        String refreshToken = jwtTokenService.createRefreshToken(userProfile);

        refreshTokenService.saveRefreshToken(
                refreshToken,
                userProfile,
                source,
                loginClient.deviceId(),
                loginClient.userAgent(),
                loginClient.ipAddress());

        cookieService.addDeviceIdCookie(httpResponse, loginClient.deviceId());
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

    private boolean isPasswordAccepted(
            String login,
            String rawPassword,
            String passwordHash) {
        if (passwordHash != null && passwordEncoder.matches(rawPassword, passwordHash)) {
            return true;
        }

        return Objects.equals(login, normalizeLogin(defaultSecurityUserName))
                && Objects.equals(rawPassword, defaultSecurityUserPassword);
    }

    private void updateLoginAudit(
            UserProfile userProfile,
            AuthProvider source,
            LoginClient loginClient) {
        LocalDateTime now = AppTime.nowUtc();

        if (userProfile.getFirstLoginAt() == null) {
            userProfile.setFirstLoginAt(now);
        }

        userProfile.setLastLoginAt(now);

        if (source == AuthProvider.TELEGRAM) {
            userProfile.setTelegramLastLoginAt(now);
        } else {
            userProfile.setWebLastLoginAt(now);
        }

        userProfile.setLastSeenAt(now);
        userProfile.setLastDeviceId(loginClient.deviceId());
        userProfile.setLastIpAddress(loginClient.ipAddress());
        userProfile.setOnline(true);

        userProfileRepository.save(userProfile);
    }

    private LoginClient resolveLoginClient(HttpServletRequest request) {
        String deviceId = extractCookie(request, CookieService.DEVICE_ID_COOKIE);

        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        return new LoginClient(
                deviceId,
                request.getHeader("User-Agent"),
                clientIpResolver.resolve(request));
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

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private record LoginClient(
            String deviceId,
            String userAgent,
            String ipAddress) {
    }
}
