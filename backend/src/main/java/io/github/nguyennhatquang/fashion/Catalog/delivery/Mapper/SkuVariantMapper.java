package io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantResponse;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;

public interface SkuVariantMapper {

    // 1. Map Create -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SkuVariant toEntity(SkuVariantRequest.SkuVariantCreateRequest request);

    // 2. Map Update -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true) // Tuyệt đối cấm cập nhật Product ID
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(SkuVariantRequest.SkuVariantUpdateRequest request, @MappingTarget SkuVariant skuVariant);

    // 3. Map Entity -> Response
    SkuVariantResponse toResponse(SkuVariant skuVariant);

    // 4. Map List (Dùng khi lấy danh sách SKU theo Product)
    List<SkuVariantResponse> toResponseList(List<SkuVariant> skuVariants);
}