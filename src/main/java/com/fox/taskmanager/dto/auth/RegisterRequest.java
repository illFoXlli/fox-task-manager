package com.fox.taskmanager.dto.auth;

import com.fox.taskmanager.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Size(min = AppConstants.Auth.LOGIN_MIN_LENGTH, max = AppConstants.Auth.LOGIN_MAX_LENGTH)
    private String login;

    @Size(max = AppConstants.Auth.EMAIL_MAX_LENGTH)
    private String email;

    @NotBlank
    @Size(min = AppConstants.Auth.PASSWORD_MIN_LENGTH, max = AppConstants.Auth.PASSWORD_MAX_LENGTH)
    private String password;

    @NotBlank
    @Size(min = AppConstants.Auth.PASSWORD_MIN_LENGTH, max = AppConstants.Auth.PASSWORD_MAX_LENGTH)
    private String confirmPassword;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
