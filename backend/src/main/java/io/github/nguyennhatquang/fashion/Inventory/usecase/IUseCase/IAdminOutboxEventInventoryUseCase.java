package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminOutboxEventInventoryUseCase {
    Result<OutboxEventInventory, Exception> createOutboxEvent(OutboxEventInventoryCreateRequest request);

    Result<OutboxEventInventory, Exception> updateOutboxEventStatus(OutboxEventInventoryUpdateRequest request, UUID id);

    Result<OutboxEventInventory, Exception> getOutboxEventById(UUID id);

    Result<ExactPageResponse<OutboxEventInventory>, Exception> getAllOutboxEvents(ExactPageRequestv2 request);
}
