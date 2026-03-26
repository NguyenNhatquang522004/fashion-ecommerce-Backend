package io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;

@Mapper(componentModel = "spring",
        // Chiến lược này giúp khi Update, nếu trường nào null thì bỏ qua, không ghi đè
        // null vào DB
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true) // ID để MongoDB tự gen
    @Mapping(target = "isDeleted", constant = "false") // Mặc định khi tạo mới
    @Mapping(target = "createdAt", ignore = true) // Do Spring Data Auditing lo
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Brand toEntity(BrandCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true) // Tuyệt đối không cho phép đổi ID
    @Mapping(target = "isDeleted", ignore = true) // Không cho phép xóa qua API Update (phải dùng API Delete riêng)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(BrandUpdateRequest request, @MappingTarget Brand brand);

    // 3. Map từ Entity ra Response
    BrandResponse toResponse(Brand brand);
}