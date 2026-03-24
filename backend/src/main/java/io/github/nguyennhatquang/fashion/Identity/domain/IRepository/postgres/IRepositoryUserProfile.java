package io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

public interface IRepositoryUserProfile {
    UserProfile save(UserProfile userProfile);

    List<UserProfile> saveAll(List<UserProfile> userProfiles);

    UserProfile update(UserProfile userProfile);

    List<UserProfile> updateAll(List<UserProfile> userProfiles);

    void delete(UserProfile userProfile);

    void deleteAll(List<UserProfile> userProfiles);

    UserProfile findById(UUID id);

    UserProfile findByUserId(UUID userId);

    UserProfile findbykeycloakId(String keycloakId);

    UserProfile findbyEmail(String email);

    void deleteByEmail(String email);

    PanigationResponse<Object> getProfilesCursor(PanigationRequest request)

}