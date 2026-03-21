package io.github.nguyennhatquang.fashion.Identity.domain.IRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;

@Repository
public interface IRepositoryUserProfile extends JpaRepository<UserProfile, UUID> {

}
