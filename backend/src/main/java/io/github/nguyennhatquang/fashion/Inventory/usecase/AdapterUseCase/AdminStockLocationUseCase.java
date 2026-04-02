package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockLocationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockLocationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminStockLocationUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStockLocationUseCase implements IAdminStockLocationUseCase {
    private final IStockLocationRepository stockLocationRepository;
    private final StockLocationMapper stockLocationMapper;
    private final IWarehouseRepository warehouseRepository;

        private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "zone", "aisle", "rack");

    @Override
    public Result<StockLocation, Exception> createStockLocation(StockLocationCreateRequest request) {
        try {
            if (request.warehouseId() == null) {
                return Result.error(new Exception("Warehouse ID is required"));
            }
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            StockLocation location = stockLocationMapper.toEntity(request);
            location.setWarehouse(warehouse);
            stockLocationRepository.save(location);
            return Result.success(location);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<StockLocation, Exception> updateStockLocation(StockLocationUpdateRequest request, UUID id) {
        try {
            StockLocation location = stockLocationRepository.findById(id).orElse(null);
            if (location == null) {
                return Result.error(new Exception("StockLocation not found"));
            }
            stockLocationMapper.updateEntityFromRequest(request, location);
            stockLocationRepository.save(location);
            return Result.success(location);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteStockLocation(UUID id) {
        try {
            stockLocationRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<StockLocation, Exception> getStockLocationById(UUID id) {
        try {
            StockLocation location = stockLocationRepository.findById(id).orElse(null);
            if (location == null) {
                return Result.error(new Exception("StockLocation not found"));
            }
            return Result.success(location);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<StockLocation>, Exception> getAllStockLocations(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<StockLocation> response = paginationService.execute(
                    request,
                    StockLocation.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
