package com.fox.taskmanager.model;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.support.AppTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @Column(length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 30)
    private AuthProvider authProvider;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "telegram_username", length = 100)
    private String telegramUsername;

    @Column(name = "telegram_first_name", length = 100)
    private String telegramFirstName;

    @Column(name = "telegram_last_name", length = 100)
    private String telegramLastName;

    @Column(name = "telegram_photo_url")
    private String telegramPhotoUrl;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(nullable = false)
    private boolean online;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "first_login_at")
    private LocalDateTime firstLoginAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "web_last_login_at")
    private LocalDateTime webLastLoginAt;

    @Column(name = "telegram_last_login_at")
    private LocalDateTime telegramLastLoginAt;

    @Column(name = "last_device_id", length = 64)
    private String lastDeviceId;

    @Column(name = "last_ip_address", length = 100)
    private String lastIpAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserProfile() {
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = AppTime.nowUtc();

        createdAt = now;
        updatedAt = now;

        if (languageCode == null) {
            languageCode = AppConstants.Auth.DEFAULT_LANGUAGE_CODE;
        }

        if (role == null) {
            role = UserRole.USER;
        }

        if (authProvider == null) {
            authProvider = AuthProvider.WEB;
        }

        enabled = true;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = AppTime.nowUtc();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public LocalDateTime getFirstLoginAt() {
        return firstLoginAt;
    }

    public void setFirstLoginAt(LocalDateTime firstLoginAt) {
        this.firstLoginAt = firstLoginAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getWebLastLoginAt() {
        return webLastLoginAt;
    }

    public void setWebLastLoginAt(LocalDateTime webLastLoginAt) {
        this.webLastLoginAt = webLastLoginAt;
    }

    public LocalDateTime getTelegramLastLoginAt() {
        return telegramLastLoginAt;
    }

    public void setTelegramLastLoginAt(LocalDateTime telegramLastLoginAt) {
        this.telegramLastLoginAt = telegramLastLoginAt;
    }

    public String getLastDeviceId() {
        return lastDeviceId;
    }

    public void setLastDeviceId(String lastDeviceId) {
        this.lastDeviceId = lastDeviceId;
    }

    public String getLastIpAddress() {
        return lastIpAddress;
    }

    public void setLastIpAddress(String lastIpAddress) {
        this.lastIpAddress = lastIpAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
