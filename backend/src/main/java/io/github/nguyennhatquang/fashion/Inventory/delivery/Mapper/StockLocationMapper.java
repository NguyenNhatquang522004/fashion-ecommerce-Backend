package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockLocationMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)   // Warehouse được load trong Service
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    StockLocation toEntity(StockLocationCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(StockLocationUpdateRequest request, @MappingTarget StockLocation stockLocation);

    // 3. Map từ Entity ra Response
    @Mapping(target = "warehouseId", source = "warehouse.id")
    StockLocationResponse toResponse(StockLocation stockLocation);

    @Mapping(target = "warehouseId", source = "warehouse.id")
    List<StockLocationResponse> toResponseList(List<StockLocation> stockLocations);
}
