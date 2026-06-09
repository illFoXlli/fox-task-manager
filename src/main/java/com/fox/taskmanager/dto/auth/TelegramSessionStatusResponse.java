package com.fox.taskmanager.dto.auth;

public class TelegramSessionStatusResponse {

    private String status;

    public TelegramSessionStatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
