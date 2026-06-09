package com.fox.taskmanager.model;

import com.fox.taskmanager.support.AppTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_auth_sessions")
public class TelegramAuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TelegramAuthMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TelegramAuthStatus status;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "telegram_username", length = 100)
    private String telegramUsername;

    @Column(name = "telegram_first_name", length = 100)
    private String telegramFirstName;

    @Column(name = "telegram_last_name", length = 100)
    private String telegramLastName;

    @Column(name = "telegram_photo_url")
    private String telegramPhotoUrl;

    @Column(name = "return_base_url", length = 255)
    private String returnBaseUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = AppTime.nowUtc();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = AppTime.nowUtc();
    }

    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public TelegramAuthMode getMode() {
        return mode;
    }

    public void setMode(TelegramAuthMode mode) {
        this.mode = mode;
    }

    public TelegramAuthStatus getStatus() {
        return status;
    }

    public void setStatus(TelegramAuthStatus status) {
        this.status = status;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public String getTelegramFirstName() {
        return telegramFirstName;
    }

    public void setTelegramFirstName(String telegramFirstName) {
        this.telegramFirstName = telegramFirstName;
    }

    public String getTelegramLastName() {
        return telegramLastName;
    }

    public void setTelegramLastName(String telegramLastName) {
        this.telegramLastName = telegramLastName;
    }

    public String getTelegramPhotoUrl() {
        return telegramPhotoUrl;
    }

    public void setTelegramPhotoUrl(String telegramPhotoUrl) {
        this.telegramPhotoUrl = telegramPhotoUrl;
    }

    public String getReturnBaseUrl() {
        return returnBaseUrl;
    }

    public void setReturnBaseUrl(String returnBaseUrl) {
        this.returnBaseUrl = returnBaseUrl;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
