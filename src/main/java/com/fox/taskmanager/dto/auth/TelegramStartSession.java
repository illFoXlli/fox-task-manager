package com.fox.taskmanager.dto.auth;

public class TelegramStartSession {

    private final String token;
    private final String telegramUrl;

    public TelegramStartSession(String token, String telegramUrl) {
        this.token = token;
        this.telegramUrl = telegramUrl;
    }

    public String getToken() {
        return token;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }
}
