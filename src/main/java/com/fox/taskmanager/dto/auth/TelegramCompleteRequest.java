package com.fox.taskmanager.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class TelegramCompleteRequest {

    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
