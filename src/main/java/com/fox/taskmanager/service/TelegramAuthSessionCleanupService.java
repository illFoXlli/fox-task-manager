package com.fox.taskmanager.service;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.model.TelegramAuthStatus;
import com.fox.taskmanager.repository.TelegramAuthSessionRepository;
import com.fox.taskmanager.support.AppTime;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelegramAuthSessionCleanupService {

    private static final List<TelegramAuthStatus> ACTIVE_STATUSES = List.of(
            TelegramAuthStatus.PENDING,
            TelegramAuthStatus.AWAITING_CONFIRMATION,
            TelegramAuthStatus.CONFIRMED);
    private static final List<TelegramAuthStatus> FINISHED_STATUSES = List.of(
            TelegramAuthStatus.COMPLETED,
            TelegramAuthStatus.EXPIRED);

    private final TelegramAuthSessionRepository telegramAuthSessionRepository;

    public TelegramAuthSessionCleanupService(
            TelegramAuthSessionRepository telegramAuthSessionRepository) {
        this.telegramAuthSessionRepository = telegramAuthSessionRepository;
    }

    @Scheduled(fixedDelayString = "${app.telegram.cleanup-fixed-delay-ms:3600000}")
    @Transactional
    public void cleanup() {
        LocalDateTime now = AppTime.nowUtc();
        LocalDateTime cutoff = now.minusDays(AppConstants.Telegram.AUTH_SESSION_RETENTION_DAYS);

        telegramAuthSessionRepository.expireStaleSessions(
                now,
                TelegramAuthStatus.EXPIRED,
                ACTIVE_STATUSES);
        telegramAuthSessionRepository.deleteFinishedSessionsBefore(cutoff, FINISHED_STATUSES);
    }
}
