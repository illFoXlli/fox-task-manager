package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserProfile(UserProfile userProfile);

    void deleteAllByUserProfile(UserProfile userProfile);
}
