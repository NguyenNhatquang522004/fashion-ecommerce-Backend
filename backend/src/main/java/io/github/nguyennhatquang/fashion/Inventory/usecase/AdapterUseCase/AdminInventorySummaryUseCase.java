package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventorySummaryMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminInventorySummaryUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInventorySummaryUseCase implements IAdminInventorySummaryUseCase {
    private final IInventorySummaryRepository summaryRepository;
    private final InventorySummaryMapper summaryMapper;
    private final IWarehouseRepository warehouseRepository;

        private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "onHand", "available");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "skuCode");

    @Override
    public Result<InventorySummary, Exception> createInventorySummary(InventorySummaryCreateRequest request) {
        try {
            if (request.warehouseId() == null) {
                return Result.error(new Exception("Warehouse ID is required"));
            }
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            InventorySummary summary = summaryMapper.toEntity(request);
            summary.setWarehouse(warehouse);
            summaryRepository.save(summary);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<InventorySummary, Exception> updateInventorySummary(InventorySummaryUpdateRequest request, UUID id) {
        try {
            InventorySummary summary = summaryRepository.findById(id).orElse(null);
            if (summary == null) {
                return Result.error(new Exception("InventorySummary not found"));
            }
            summaryMapper.updateEntityFromRequest(request, summary);
            summaryRepository.save(summary);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteInventorySummary(UUID id) {
        try {
            summaryRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<InventorySummary, Exception> getInventorySummaryById(UUID id) {
        try {
            InventorySummary summary = summaryRepository.findById(id).orElse(null);
            if (summary == null) {
                return Result.error(new Exception("InventorySummary not found"));
            }
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<InventorySummary>, Exception> getAllInventorySummaries(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<InventorySummary> response = paginationService.execute(
                    request,
                    InventorySummary.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
