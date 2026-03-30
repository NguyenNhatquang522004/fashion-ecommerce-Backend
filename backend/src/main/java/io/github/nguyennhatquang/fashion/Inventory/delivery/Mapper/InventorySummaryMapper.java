package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventorySummaryMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)   // Warehouse được load trong Service
    @Mapping(target = "available", ignore = true)   // GENERATED column - DB tự tính
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    InventorySummary toEntity(InventorySummaryCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "skuCode", ignore = true)     // SKU không được đổi sau khi tạo
    @Mapping(target = "available", ignore = true)   // GENERATED column
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(InventorySummaryUpdateRequest request, @MappingTarget InventorySummary summary);

    // 3. Map từ Entity ra Response
    @Mapping(target = "warehouseId", source = "warehouse.id")
    InventorySummaryResponse toResponse(InventorySummary summary);

    @Mapping(target = "warehouseId", source = "warehouse.id")
    List<InventorySummaryResponse> toResponseList(List<InventorySummary> summaries);
}
