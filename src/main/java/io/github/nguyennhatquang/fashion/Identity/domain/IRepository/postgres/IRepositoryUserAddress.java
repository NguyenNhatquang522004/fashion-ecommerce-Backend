package io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;

@Repository
public interface IRepositoryUserAddress {
    UserAddress save(UserAddress userAddress);
    List<UserAddress> saveAll(List<UserAddress> userAddresses);
    UserAddress update(UserAddress userAddress);
    List<UserAddress> updateAll(List<UserAddress> userAddresses);
    boolean delete(UserAddress userAddress);
    boolean deleteAll(List<UserAddress> userAddresses);
    Optional<UserAddress> findById(UUID id);
    List<UserAddress> findByUserId(UUID userId);

}
