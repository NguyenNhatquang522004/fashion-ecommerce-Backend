package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;

@Repository
public interface UserProfileJpaRepo extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);

    Optional<UserProfile> findByKeycloakId(String keycloakId);

    Optional<UserProfile> findByEmail(String email);
}
