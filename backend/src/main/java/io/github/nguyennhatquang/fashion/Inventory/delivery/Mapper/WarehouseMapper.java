package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Warehouse toEntity(WarehouseCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)        // Mã kho không được đổi sau khi tạo
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(WarehouseUpdateRequest request, @MappingTarget Warehouse warehouse);

    // 3. Map từ Entity ra Response
    WarehouseResponse toResponse(Warehouse warehouse);

    List<WarehouseResponse> toResponseList(List<Warehouse> warehouses);
}
