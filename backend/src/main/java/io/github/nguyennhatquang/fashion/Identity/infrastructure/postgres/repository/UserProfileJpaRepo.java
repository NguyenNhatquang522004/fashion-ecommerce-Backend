package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;

@Repository
public interface UserProfileJpaRepo extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);

    Optional<UserProfile> findByKeycloakId(String keycloakId);

    Optional<UserProfile> findByEmail(String email);

    // 1. Fetch NEXT page (DESC)
    @Query("SELECT u FROM UserProfile u WHERE " +
            "(:cursorTime IS NULL OR u.createdAt < :cursorTime OR (u.createdAt = :cursorTime AND u.id < :cursorId)) " +
            "ORDER BY u.createdAt DESC, u.id DESC")
    List<UserProfile> findNextPageDesc(
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    // 2. Fetch PREVIOUS page (DESC list) -> Query ngược lại (ASC) để lấy phần tử
    // phía trước
    @Query("SELECT u FROM UserProfile u WHERE " +
            "u.createdAt > :cursorTime OR (u.createdAt = :cursorTime AND u.id > :cursorId) " +
            "ORDER BY u.createdAt ASC, u.id ASC")
    List<UserProfile> findPreviousPageDesc(
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
