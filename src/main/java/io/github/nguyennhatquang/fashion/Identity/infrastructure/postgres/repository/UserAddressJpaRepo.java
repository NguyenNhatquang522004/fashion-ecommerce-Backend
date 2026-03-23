package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;

@Repository
public interface UserAddressJpaRepo extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserProfile_Id(UUID userId);

}
