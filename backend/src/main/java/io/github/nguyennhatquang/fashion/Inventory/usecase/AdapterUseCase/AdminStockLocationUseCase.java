package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockLocationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockLocationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminStockLocationUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
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

    @Override
    public Result<StockLocation, Exception> createStockLocation(StockLocationCreateRequest request) {
        try {
            StockLocation location = stockLocationMapper.toEntity(request);
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
    public Result<ExactPageResponse<StockLocation>, Exception> getAllStockLocations(ExactPageRequest request) {
        try {
            ExactPageResponse<StockLocation> locations = stockLocationRepository.getStockLocationExactPage(request);
            return Result.success(locations);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
