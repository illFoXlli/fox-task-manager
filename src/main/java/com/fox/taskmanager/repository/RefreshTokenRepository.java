package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserProfile(UserProfile userProfile);

    @Query("""
            select token
            from RefreshToken token
            where token.userProfile = :userProfile
              and token.source = :source
              and token.deviceId = :deviceId
            order by token.updatedAt desc, token.id desc
            """)
    List<RefreshToken> findCurrentDeviceTokens(
            @Param("userProfile") UserProfile userProfile,
            @Param("source") AuthProvider source,
            @Param("deviceId") String deviceId);

    @Query("""
            select token
            from RefreshToken token
            where token.userProfile = :userProfile
              and token.source = :source
              and token.deviceId like 'legacy-%'
              and (
                    (:userAgent is null and token.userAgent is null)
                    or token.userAgent = :userAgent
              )
            order by token.updatedAt desc, token.id desc
            """)
    List<RefreshToken> findLegacyCurrentDeviceTokens(
            @Param("userProfile") UserProfile userProfile,
            @Param("source") AuthProvider source,
            @Param("userAgent") String userAgent);

    void deleteAllByUserProfile(UserProfile userProfile);
}
