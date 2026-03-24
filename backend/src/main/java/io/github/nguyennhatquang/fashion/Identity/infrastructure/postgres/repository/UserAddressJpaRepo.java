package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;

@Repository
public interface UserAddressJpaRepo extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserProfile_Id(UUID userId);

   // 1. Fetch NEXT page (DESC) - ĐÃ BỔ SUNG userId
    @Query("SELECT u FROM UserAddress u WHERE u.userProfile.id = :userId AND " +
            "(:cursorTime IS NULL OR u.createdAt < :cursorTime OR (u.createdAt = :cursorTime AND u.id < :cursorId)) " +
            "ORDER BY u.createdAt DESC, u.id DESC")
    List<UserAddress> findNextPageDesc(
            @Param("userId") UUID userId,
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    // 2. Fetch PREVIOUS page (DESC list) - ĐÃ BỔ SUNG userId
    @Query("SELECT u FROM UserAddress u WHERE u.userProfile.id = :userId AND " +
            "(u.createdAt > :cursorTime OR (u.createdAt = :cursorTime AND u.id > :cursorId)) " +
            "ORDER BY u.createdAt ASC, u.id ASC")
    List<UserAddress> findPreviousPageDesc(
            @Param("userId") UUID userId,
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

}
