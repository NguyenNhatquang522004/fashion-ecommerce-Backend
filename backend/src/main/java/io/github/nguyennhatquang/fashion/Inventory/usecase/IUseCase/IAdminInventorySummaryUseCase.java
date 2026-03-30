package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminInventorySummaryUseCase {
    Result<InventorySummary, Exception> createInventorySummary(InventorySummaryCreateRequest request);

    Result<InventorySummary, Exception> updateInventorySummary(InventorySummaryUpdateRequest request, UUID id);

    Result<Void, Exception> deleteInventorySummary(UUID id);

    Result<InventorySummary, Exception> getInventorySummaryById(UUID id);

    Result<ExactPageResponse<InventorySummary>, Exception> getAllInventorySummaries(ExactPageRequest request);
}
