package com.fox.taskmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.telegram")
public class TelegramProperties {

    private String botUsername;
    private String botToken;
    private String returnBaseUrl;
    private String webhookSecret;
    private long authMaxAgeSeconds = 86400;

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getReturnBaseUrl() {
        return returnBaseUrl;
    }

    public void setReturnBaseUrl(String returnBaseUrl) {
        this.returnBaseUrl = returnBaseUrl;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public long getAuthMaxAgeSeconds() {
        return authMaxAgeSeconds;
    }

    public void setAuthMaxAgeSeconds(long authMaxAgeSeconds) {
        this.authMaxAgeSeconds = authMaxAgeSeconds;
    }
}
