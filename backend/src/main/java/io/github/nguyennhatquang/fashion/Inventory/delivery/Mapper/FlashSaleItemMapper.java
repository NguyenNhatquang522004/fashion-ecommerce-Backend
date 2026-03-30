package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FlashSaleItemMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "campaign", ignore = true)    // Campaign được set trong Service layer
    @Mapping(target = "purchaseLimitPerUser", ignore = true) // Sẽ lấy từ request hoặc default
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    FlashSaleItem toEntity(FlashSaleItemCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "skuCode", ignore = true)     // SKU không được đổi sau khi tạo
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(FlashSaleItemUpdateRequest request, @MappingTarget FlashSaleItem item);

    // 3. Map từ Entity ra Response
    @Mapping(target = "campaignId", source = "campaign.id")
    FlashSaleItemResponse toResponse(FlashSaleItem item);

    @Mapping(target = "campaignId", source = "campaign.id")
    List<FlashSaleItemResponse> toResponseList(List<FlashSaleItem> items);
}
