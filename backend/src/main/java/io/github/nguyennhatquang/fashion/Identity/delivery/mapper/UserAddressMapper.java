package io.github.nguyennhatquang.fashion.Identity.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressRequest;
import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressResponse;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAddressMapper {
    // 1. Chuyển từ Request sang Entity để Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    UserAddress toEntity(UserAddressRequest request);

    // 2. Cập nhật dữ liệu từ Request vào Entity có sẵn (Update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntityFromRequest(UserAddressRequest request, @MappingTarget UserAddress entity);

    // 3. Chuyển từ Entity sang Response
    UserAddressResponse toResponse(UserAddress entity);
}
