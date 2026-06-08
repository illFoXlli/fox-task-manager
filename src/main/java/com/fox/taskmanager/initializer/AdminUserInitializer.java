package com.fox.taskmanager.initializer;

import com.fox.taskmanager.config.AdminProperties;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import com.fox.taskmanager.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    public AdminUserInitializer(
            AdminProperties adminProperties,
            PasswordEncoder passwordEncoder,
            UserProfileRepository userProfileRepository) {
        this.adminProperties = adminProperties;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void run(String... args) {
        if (userProfileRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        String login = normalizeLogin(adminProperties.getLogin());

        if (login.isBlank() || adminProperties.getPassword() == null
                || adminProperties.getPassword().isBlank()) {
            throw new IllegalStateException("Admin credentials are not configured");
        }

        UserProfile admin = new UserProfile();
        admin.setLogin(login);
        admin.setEmail(emptyToNull(adminProperties.getEmail()));
        admin.setDisplayName(adminProperties.getDisplayName());
        admin.setPasswordHash(passwordEncoder.encode(adminProperties.getPassword()));
        admin.setRole(UserRole.ADMIN);
        admin.setAuthProvider(AuthProvider.WEB);
        admin.setLanguageCode("uk");
        admin.setEnabled(true);
        admin.setAccountLocked(false);
        admin.setOnline(false);

        userProfileRepository.save(admin);
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
