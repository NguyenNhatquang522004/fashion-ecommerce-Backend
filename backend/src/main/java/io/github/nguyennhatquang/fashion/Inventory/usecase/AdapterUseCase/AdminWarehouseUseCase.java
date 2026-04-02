package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse.WarehouseRequest.WarehouseUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.WarehouseMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminWarehouseUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminWarehouseUseCase implements IAdminWarehouseUseCase {
    private final IWarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

        private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "code", "name");

    @Override
    public Result<Warehouse, Exception> createWarehouse(WarehouseCreateRequest request) {
        try {
            Warehouse warehouse = warehouseMapper.toEntity(request);
            warehouseRepository.save(warehouse);
            return Result.success(warehouse);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Warehouse, Exception> updateWarehouse(WarehouseUpdateRequest request, UUID id) {
        try {
            Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            warehouseMapper.updateEntityFromRequest(request, warehouse);
            warehouseRepository.save(warehouse);
            return Result.success(warehouse);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteWarehouse(UUID id) {
        try {
            warehouseRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Warehouse, Exception> getWarehouseById(UUID id) {
        try {
            Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            return Result.success(warehouse);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Warehouse>, Exception> getAllWarehouses(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<Warehouse> response = paginationService.execute(
                    request,
                    Warehouse.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
