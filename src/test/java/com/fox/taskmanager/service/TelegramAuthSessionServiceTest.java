package com.fox.taskmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.config.TelegramProperties;
import com.fox.taskmanager.dto.telegram.TelegramBotUser;
import com.fox.taskmanager.model.TelegramAuthMode;
import com.fox.taskmanager.model.TelegramAuthSession;
import com.fox.taskmanager.model.TelegramAuthStatus;
import com.fox.taskmanager.repository.TelegramAuthSessionRepository;
import com.fox.taskmanager.repository.UserProfileRepository;
import com.fox.taskmanager.security.ClientIpResolver;
import com.fox.taskmanager.security.CookieService;
import com.fox.taskmanager.security.JwtTokenService;
import com.fox.taskmanager.security.TokenHashService;
import com.fox.taskmanager.support.AppTime;
import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TelegramAuthSessionServiceTest {

    private static final String TOKEN = "telegram-session-token";
    private static final String TOKEN_HASH = "telegram-session-token-hash";
    private static final long SESSION_ID = 42L;
    private static final long TELEGRAM_ID = 1020763635L;

    private final ClientIpResolver clientIpResolver = mock(ClientIpResolver.class);
    private final CookieService cookieService = mock(CookieService.class);
    private final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
    private final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
    private final TelegramAuthSessionRepository telegramAuthSessionRepository = mock(
            TelegramAuthSessionRepository.class);
    private final TelegramProperties telegramProperties = mock(TelegramProperties.class);
    private final TokenHashService tokenHashService = mock(TokenHashService.class);
    private final UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);

    @Test
    void registerStartExpiresOtherActiveSessionsForSameTelegramUserAndMode() {
        TelegramAuthSession session = pendingLoginSession();
        TelegramBotUser user = telegramUser();

        when(tokenHashService.hash(TOKEN)).thenReturn(TOKEN_HASH);
        when(telegramAuthSessionRepository.findByTokenHash(TOKEN_HASH))
                .thenReturn(Optional.of(session));

        telegramAuthSessionService().registerStart(TOKEN, user);

        verify(telegramAuthSessionRepository).expireActiveSessionsForTelegramUser(
                eq(SESSION_ID),
                eq(TELEGRAM_ID),
                eq(TelegramAuthMode.LOGIN),
                eq(TelegramAuthStatus.EXPIRED),
                anyStatusCollection());
        verify(telegramAuthSessionRepository).save(session);
    }

    private TelegramAuthSessionService telegramAuthSessionService() {
        return new TelegramAuthSessionService(
                clientIpResolver,
                cookieService,
                jwtTokenService,
                refreshTokenService,
                telegramAuthSessionRepository,
                telegramProperties,
                tokenHashService,
                userProfileRepository);
    }

    private TelegramAuthSession pendingLoginSession() {
        TelegramAuthSession session = new TelegramAuthSession();

        ReflectionTestUtils.setField(session, "id", SESSION_ID);
        session.setMode(TelegramAuthMode.LOGIN);
        session.setStatus(TelegramAuthStatus.PENDING);
        session.setExpiresAt(AppTime.nowUtc().plusMinutes(5));

        return session;
    }

    private TelegramBotUser telegramUser() {
        TelegramBotUser user = new TelegramBotUser();

        user.setId(TELEGRAM_ID);
        user.setUsername("lliFoXill");

        return user;
    }

    private Collection<TelegramAuthStatus> anyStatusCollection() {
        return any();
    }
}
