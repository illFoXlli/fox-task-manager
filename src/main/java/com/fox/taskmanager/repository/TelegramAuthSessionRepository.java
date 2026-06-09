package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.TelegramAuthSession;
import com.fox.taskmanager.model.TelegramAuthStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelegramAuthSessionRepository extends JpaRepository<TelegramAuthSession, Long> {

    Optional<TelegramAuthSession> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update TelegramAuthSession session
            set session.status = :expiredStatus
            where session.status in :activeStatuses
              and session.expiresAt < :now
            """)
    int expireStaleSessions(
            @Param("now") LocalDateTime now,
            @Param("expiredStatus") TelegramAuthStatus expiredStatus,
            @Param("activeStatuses") Collection<TelegramAuthStatus> activeStatuses);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from TelegramAuthSession session
            where session.status in :finishedStatuses
              and session.updatedAt < :cutoff
            """)
    int deleteFinishedSessionsBefore(
            @Param("cutoff") LocalDateTime cutoff,
            @Param("finishedStatuses") Collection<TelegramAuthStatus> finishedStatuses);
}
