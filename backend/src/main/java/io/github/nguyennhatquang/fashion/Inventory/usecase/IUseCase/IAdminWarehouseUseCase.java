package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminWarehouseUseCase {
    Result<Warehouse, Exception> createWarehouse(WarehouseCreateRequest request);

    Result<Warehouse, Exception> updateWarehouse(WarehouseUpdateRequest request, UUID id);

    Result<Void, Exception> deleteWarehouse(UUID id);

    Result<Warehouse, Exception> getWarehouseById(UUID id);

    Result<List<Warehouse>, Exception> getAllWarehouses();
}
