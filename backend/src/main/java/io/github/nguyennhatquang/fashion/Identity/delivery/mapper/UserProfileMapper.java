package io.github.nguyennhatquang.fashion.Identity.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UpdatedUserProfile.UpdateProfileRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    /**
     * Map DTO -> Entity
     * Lưu ý: Không map các trường Auditing (createdBy, createdAt, version,
     * isDeleted)
     * và các trường không có trong DTO (keycloakId, otp, typeLogin, loyaltyTier)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "loyaltyTier", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateProfileFromDto(UpdateProfileRequest dto, @MappingTarget UserProfile entity);
}
