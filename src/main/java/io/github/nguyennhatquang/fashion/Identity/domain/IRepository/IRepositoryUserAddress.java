package io.github.nguyennhatquang.fashion.Identity.domain.IRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;

@Repository
public interface IRepositoryUserAddress extends JpaRepository<UserAddress, UUID> {

}
