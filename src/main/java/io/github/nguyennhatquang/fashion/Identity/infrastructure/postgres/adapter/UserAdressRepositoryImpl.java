package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserAddress;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;
import io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository.UserAddressJpaRepo;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAdressRepositoryImpl implements IRepositoryUserAddress {
    private final UserAddressJpaRepo userAddressJpaRepo;

    @Override
    public UserAddress save(UserAddress userAddress) {
        return userAddressJpaRepo.save(userAddress);
    }

    @Override
    public List<UserAddress> saveAll(List<UserAddress> userAddresses) {
        return userAddressJpaRepo.saveAll(userAddresses);
    }

    @Override
    public UserAddress update(UserAddress userAddress) {
        return userAddressJpaRepo.save(userAddress);
    }

    @Override
    public List<UserAddress> updateAll(List<UserAddress> userAddresses) {
        return userAddressJpaRepo.saveAll(userAddresses);
    }

    @Override
    public boolean delete(UserAddress userAddress) {
        userAddressJpaRepo.delete(userAddress);
        return true;
    }

    @Override
    public boolean deleteAll(List<UserAddress> userAddresses) {
        userAddressJpaRepo.deleteAll(userAddresses);
        return true;
    }

    @Override
    public Optional<UserAddress> findById(UUID id) {
        return userAddressJpaRepo.findById(id);
    }

    @Override
    public List<UserAddress> findByUserId(UUID userId) {
        return userAddressJpaRepo.findByUserProfile_Id(userId);
    }

}
