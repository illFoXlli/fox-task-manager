package com.fox.taskmanager.dto.auth;

import com.fox.taskmanager.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank
    @Size(min = AppConstants.Auth.LOGIN_MIN_LENGTH, max = AppConstants.Auth.LOGIN_MAX_LENGTH)
    private String login;

    @NotBlank
    @Size(min = AppConstants.Auth.PASSWORD_MIN_LENGTH, max = AppConstants.Auth.PASSWORD_MAX_LENGTH)
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
