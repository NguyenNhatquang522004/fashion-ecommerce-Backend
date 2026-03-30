package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;

import java.util.List;

/**
 * InventoryLedger là append-only (immutable), không cần updateEntityFromRequest.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryLedgerMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)   // Warehouse được load trong Service
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    InventoryLedger toEntity(InventoryLedgerCreateRequest request);

    // 2. Map từ Entity ra Response
    @Mapping(target = "warehouseId", source = "warehouse.id")
    InventoryLedgerResponse toResponse(InventoryLedger ledger);

    @Mapping(target = "warehouseId", source = "warehouse.id")
    List<InventoryLedgerResponse> toResponseList(List<InventoryLedger> ledgers);
}
