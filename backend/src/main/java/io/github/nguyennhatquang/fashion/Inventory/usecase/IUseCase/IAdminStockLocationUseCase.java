package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminStockLocationUseCase {
    Result<StockLocation, Exception> createStockLocation(StockLocationCreateRequest request);

    Result<StockLocation, Exception> updateStockLocation(StockLocationUpdateRequest request, UUID id);

    Result<Void, Exception> deleteStockLocation(UUID id);

    Result<StockLocation, Exception> getStockLocationById(UUID id);

    Result<List<StockLocation>, Exception> getAllStockLocations();
}
