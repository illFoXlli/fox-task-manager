package com.fox.taskmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.fox.taskmanager.model.TelegramAuthStatus;
import com.fox.taskmanager.repository.TelegramAuthSessionRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramAuthSessionCleanupServiceTest {

    @Mock
    private TelegramAuthSessionRepository telegramAuthSessionRepository;

    @Test
    void cleanupExpiresActiveSessionsAndDeletesOldFinishedSessions() {
        TelegramAuthSessionCleanupService cleanupService = new TelegramAuthSessionCleanupService(
                telegramAuthSessionRepository);

        cleanupService.cleanup();

        verify(telegramAuthSessionRepository).expireStaleSessions(
                any(LocalDateTime.class),
                eq(TelegramAuthStatus.EXPIRED),
                anyStatusCollection());
        verify(telegramAuthSessionRepository).deleteFinishedSessionsBefore(
                any(LocalDateTime.class),
                anyStatusCollection());
    }

    private Collection<TelegramAuthStatus> anyStatusCollection() {
        return any();
    }
}
