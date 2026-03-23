package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository.UserProfileJpaRepo;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements IRepositoryUserProfile {
    private final UserProfileJpaRepo userProfileJpaRepo;

    @Override
    public UserProfile save(UserProfile userProfile) {
        return userProfileJpaRepo.save(userProfile);
    }

    @Override
    public List<UserProfile> saveAll(List<UserProfile> userProfiles) {
        return userProfileJpaRepo.saveAll(userProfiles);
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        return userProfileJpaRepo.save(userProfile);
    }

    @Override
    public List<UserProfile> updateAll(List<UserProfile> userProfiles) {
        return userProfileJpaRepo.saveAll(userProfiles);
    }

    @Override
    public void delete(UserProfile userProfile) {
        userProfileJpaRepo.delete(userProfile);
    }

    @Override
    public void deleteAll(List<UserProfile> userProfiles) {
        userProfileJpaRepo.deleteAll(userProfiles);
    }

    @Override
    public UserProfile findById(UUID id) {
        return userProfileJpaRepo.findById(id).orElse(null);
    }

    @Override
    public UserProfile findByUserId(UUID userId) {
        return userProfileJpaRepo.findByUserId(userId).orElse(null);
    }

    @Override
    public UserProfile findbykeycloakId(String keycloakId) {
        return userProfileJpaRepo.findByKeycloakId(keycloakId).orElse(null);
    }

    @Override
    public UserProfile findbyEmail(String email) {
        return userProfileJpaRepo.findByEmail(email).orElse(null);
    }

    @Override
    public void deleteByEmail(String email) {
        UserProfile userProfile = userProfileJpaRepo.findByEmail(email).orElse(null);
        if (userProfile != null) {
            userProfileJpaRepo.delete(userProfile);
        }
    }
}
