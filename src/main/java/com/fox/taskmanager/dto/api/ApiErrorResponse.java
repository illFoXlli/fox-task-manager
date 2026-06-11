package com.fox.taskmanager.dto.api;

import com.fox.taskmanager.support.AppTime;

public class ApiErrorResponse {

    private final String message;
    private final String path;
    private final int status;
    private final String timestamp;

    public ApiErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = AppTime.toUtcString(AppTime.nowUtc());
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
