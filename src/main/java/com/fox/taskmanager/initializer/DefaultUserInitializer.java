package com.fox.taskmanager.initializer;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.DefaultUserProperties;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private final DefaultUserProperties defaultUserProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    public DefaultUserInitializer(
            DefaultUserProperties defaultUserProperties,
            PasswordEncoder passwordEncoder,
            UserProfileRepository userProfileRepository) {
        this.defaultUserProperties = defaultUserProperties;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void run(String... args) {
        if (!defaultUserProperties.isEnabled()) {
            return;
        }

        String login = normalizeLogin(defaultUserProperties.getLogin());

        if (login.isBlank() || defaultUserProperties.getPassword() == null
                || defaultUserProperties.getPassword().isBlank()) {
            throw new IllegalStateException("Default user credentials are not configured");
        }

        UserProfile userProfile = userProfileRepository.findByLogin(login)
                .orElseGet(UserProfile::new);

        userProfile.setLogin(login);
        userProfile.setEmail(emptyToNull(defaultUserProperties.getEmail()));
        userProfile.setDisplayName(defaultUserProperties.getDisplayName());
        userProfile.setLanguageCode(AppConstants.Auth.DEFAULT_LANGUAGE_CODE);
        userProfile.setRole(UserRole.USER);
        userProfile.setAuthProvider(AuthProvider.WEB);
        userProfile.setEnabled(true);
        userProfile.setAccountLocked(false);
        userProfile.setOnline(false);

        if (defaultUserProperties.isUpdatePassword() || userProfile.getPasswordHash() == null) {
            userProfile
                    .setPasswordHash(passwordEncoder.encode(defaultUserProperties.getPassword()));
        }

        userProfileRepository.save(userProfile);
    }

    private String normalizeLogin(String login) {
        if (login == null) {
            return "";
        }

        return login.trim().toLowerCase();
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
