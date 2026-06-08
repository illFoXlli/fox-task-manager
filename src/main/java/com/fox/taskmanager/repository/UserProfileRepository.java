package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.model.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByLogin(String login);

    Optional<UserProfile> findByTelegramId(Long telegramId);

    boolean existsByLogin(String login);

    boolean existsByRole(UserRole role);
}
