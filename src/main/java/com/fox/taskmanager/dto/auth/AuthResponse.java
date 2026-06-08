package com.fox.taskmanager.dto.auth;

public class AuthResponse {

    private String message;
    private String redirectUrl;

    public AuthResponse(String message, String redirectUrl) {
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
