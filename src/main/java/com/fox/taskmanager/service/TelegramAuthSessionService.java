package com.fox.taskmanager.service;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.TelegramProperties;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.TelegramSessionStatusResponse;
import com.fox.taskmanager.dto.auth.TelegramStartSession;
import com.fox.taskmanager.dto.telegram.TelegramBotUser;
import com.fox.taskmanager.exception.AuthException;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.TelegramAuthMode;
import com.fox.taskmanager.model.TelegramAuthSession;
import com.fox.taskmanager.model.TelegramAuthStatus;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.TelegramAuthSessionRepository;
import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.security.ClientIpResolver;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import com.fox.taskmanager.security.TokenHashService;
import com.fox.taskmanager.support.AppTime;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelegramAuthSessionService {

    private static final int TOKEN_BYTE_LENGTH = 24;

    private final ClientIpResolver clientIpResolver;
    private final CookieService cookieService;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final TelegramAuthSessionRepository telegramAuthSessionRepository;
    private final TelegramProperties telegramProperties;
    private final TokenHashService tokenHashService;
    private final UserProfileRepository userProfileRepository;

    public TelegramAuthSessionService(
            ClientIpResolver clientIpResolver,
            CookieService cookieService,
            JwtTokenService jwtTokenService,
            RefreshTokenService refreshTokenService,
            TelegramAuthSessionRepository telegramAuthSessionRepository,
            TelegramProperties telegramProperties,
            TokenHashService tokenHashService,
            UserProfileRepository userProfileRepository) {
        this.clientIpResolver = clientIpResolver;
        this.cookieService = cookieService;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.telegramAuthSessionRepository = telegramAuthSessionRepository;
        this.telegramProperties = telegramProperties;
        this.tokenHashService = tokenHashService;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public TelegramStartSession createSession(String mode, String returnBaseUrl) {
        String token = generateToken();
        TelegramAuthSession session = new TelegramAuthSession();
        session.setTokenHash(tokenHashService.hash(token));
        session.setMode(resolveMode(mode));
        session.setStatus(TelegramAuthStatus.PENDING);
        session.setReturnBaseUrl(emptyToNull(returnBaseUrl));
        session.setExpiresAt(AppTime.nowUtc()
                .plusMinutes(AppConstants.Telegram.AUTH_SESSION_EXPIRATION_MINUTES));

        telegramAuthSessionRepository.save(session);

        return new TelegramStartSession(token, buildTelegramUrl(token));
    }

    @Transactional
    public TelegramSessionStatusResponse getStatus(String token) {
        TelegramAuthSession session = findSession(token);
        expireSessionIfNeeded(session);

        return new TelegramSessionStatusResponse(session.getStatus().name());
    }

    @Transactional
    public void registerStart(String token, TelegramBotUser user) {
        TelegramAuthSession session = findSession(token);
        validatePendingSession(session);
        updateSessionTelegramUser(session, user);
        session.setStatus(TelegramAuthStatus.AWAITING_CONFIRMATION);

        telegramAuthSessionRepository.save(session);
    }

    @Transactional
    public String confirm(String token, TelegramBotUser user) {
        TelegramAuthSession session = findSession(token);
        validatePendingSession(session);
        validateSameTelegramUser(session, user);

        UserProfile userProfile = userProfileRepository.findByTelegramId(user.getId())
                .orElseGet(() -> createTelegramUserProfile(user));
        updateUserTelegramProfile(userProfile, user);

        session.setUserProfile(userProfileRepository.save(userProfile));
        session.setStatus(TelegramAuthStatus.CONFIRMED);
        session.setConfirmedAt(AppTime.nowUtc());

        telegramAuthSessionRepository.save(session);

        return buildReturnUrl(session, token);
    }

    @Transactional
    public AuthResponse complete(
            String token,
            HttpServletRequest request,
            HttpServletResponse response) {
        TelegramAuthSession session = findSession(token);
        expireSessionIfNeeded(session);

        if (session.getStatus() != TelegramAuthStatus.CONFIRMED
                || session.getUserProfile() == null) {
            throw new AuthException(AppConstants.Auth.TELEGRAM_AUTH_FAILED_MESSAGE);
        }

        UserProfile userProfile = session.getUserProfile();
        validateUserAccess(userProfile);

        LoginClient loginClient = resolveLoginClient(request);
        updateLoginAudit(userProfile, loginClient);
        issueTokens(userProfile, loginClient, response);

        session.setStatus(TelegramAuthStatus.COMPLETED);
        telegramAuthSessionRepository.save(session);

        return new AuthResponse(
                AppConstants.Auth.LOGIN_SUCCESS_MESSAGE,
                resolveRedirect(session));
    }

    private void issueTokens(
            UserProfile userProfile,
            LoginClient loginClient,
            HttpServletResponse httpResponse) {
        String accessToken = jwtTokenService.createAccessToken(userProfile);
        String refreshToken = jwtTokenService.createRefreshToken(userProfile);

        refreshTokenService.saveRefreshToken(
                refreshToken,
                userProfile,
                AuthProvider.TELEGRAM,
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

    private void updateLoginAudit(UserProfile userProfile, LoginClient loginClient) {
        LocalDateTime now = AppTime.nowUtc();

        if (userProfile.getFirstLoginAt() == null) {
            userProfile.setFirstLoginAt(now);
        }

        userProfile.setLastLoginAt(now);
        userProfile.setTelegramLastLoginAt(now);
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

    private TelegramAuthSession findSession(String token) {
        return telegramAuthSessionRepository.findByTokenHash(tokenHashService.hash(token))
                .orElseThrow(
                        () -> new AuthException(AppConstants.Auth.TELEGRAM_AUTH_FAILED_MESSAGE));
    }

    private void validatePendingSession(TelegramAuthSession session) {
        expireSessionIfNeeded(session);

        if (session.getStatus() == TelegramAuthStatus.EXPIRED) {
            throw new AuthException(AppConstants.Auth.TELEGRAM_AUTH_FAILED_MESSAGE);
        }
    }

    private void expireSessionIfNeeded(TelegramAuthSession session) {
        if (session.getStatus() == TelegramAuthStatus.COMPLETED
                || session.getStatus() == TelegramAuthStatus.EXPIRED) {
            return;
        }

        if (session.getExpiresAt().isBefore(AppTime.nowUtc())) {
            session.setStatus(TelegramAuthStatus.EXPIRED);
            telegramAuthSessionRepository.save(session);
        }
    }

    private void validateSameTelegramUser(
            TelegramAuthSession session,
            TelegramBotUser user) {
        if (session.getTelegramId() != null && !session.getTelegramId().equals(user.getId())) {
            throw new AuthException(AppConstants.Auth.TELEGRAM_AUTH_FAILED_MESSAGE);
        }

        updateSessionTelegramUser(session, user);
    }

    private void updateSessionTelegramUser(
            TelegramAuthSession session,
            TelegramBotUser user) {
        session.setTelegramId(user.getId());
        session.setTelegramUsername(emptyToNull(user.getUsername()));
        session.setTelegramFirstName(emptyToNull(user.getFirstName()));
        session.setTelegramLastName(emptyToNull(user.getLastName()));
    }

    private UserProfile createTelegramUserProfile(TelegramBotUser user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(buildTelegramLogin(user.getId()));
        userProfile.setDisplayName(buildTelegramDisplayName(user));
        userProfile.setLanguageCode(AppConstants.Auth.DEFAULT_LANGUAGE_CODE);
        userProfile.setRole(UserRole.USER);
        userProfile.setAuthProvider(AuthProvider.TELEGRAM);
        userProfile.setEnabled(true);
        userProfile.setAccountLocked(false);
        userProfile.setOnline(false);

        return userProfile;
    }

    private void updateUserTelegramProfile(
            UserProfile userProfile,
            TelegramBotUser user) {
        userProfile.setTelegramId(user.getId());
        userProfile.setTelegramUsername(emptyToNull(user.getUsername()));
        userProfile.setTelegramFirstName(emptyToNull(user.getFirstName()));
        userProfile.setTelegramLastName(emptyToNull(user.getLastName()));

        if (userProfile.getDisplayName() == null || userProfile.getDisplayName().isBlank()) {
            userProfile.setDisplayName(buildTelegramDisplayName(user));
        }
    }

    private TelegramAuthMode resolveMode(String mode) {
        if ("register".equals(mode)) {
            return TelegramAuthMode.REGISTER;
        }

        return TelegramAuthMode.LOGIN;
    }

    private String resolveRedirect(TelegramAuthSession session) {
        if (session.getMode() == TelegramAuthMode.REGISTER) {
            return AppConstants.Route.NOTE_LIST;
        }

        return AppConstants.Route.NOTE_VIEW;
    }

    private String buildTelegramUrl(String token) {
        return AppConstants.Telegram.TELEGRAM_LINK_BASE_URL
                + telegramProperties.getBotUsername()
                + "?start="
                + AppConstants.Telegram.START_PAYLOAD_PREFIX
                + token;
    }

    private String buildReturnUrl(TelegramAuthSession session, String token) {
        String baseUrl = emptyToNull(session.getReturnBaseUrl());

        if (baseUrl == null) {
            baseUrl = emptyToNull(telegramProperties.getReturnBaseUrl());
        }

        if (baseUrl == null) {
            throw new AuthException(AppConstants.Auth.TELEGRAM_AUTH_FAILED_MESSAGE);
        }

        return trimTrailingSlash(baseUrl) + "/auth/telegram/return?token=" + token;
    }

    private String trimTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    private String buildTelegramLogin(Long telegramId) {
        return "tg_" + telegramId;
    }

    private String buildTelegramDisplayName(TelegramBotUser user) {
        String firstName = emptyToNull(user.getFirstName());
        String lastName = emptyToNull(user.getLastName());
        String username = emptyToNull(user.getUsername());
        String fullName = String.join(" ",
                firstName == null ? "" : firstName,
                lastName == null ? "" : lastName)
                .trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        if (username != null) {
            return "@" + username;
        }

        return buildTelegramLogin(user.getId());
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
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
