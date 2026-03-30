package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminOutboxEventInventoryUseCase {
    Result<OutboxEventInventory, Exception> createOutboxEvent(OutboxEventInventoryCreateRequest request);

    Result<OutboxEventInventory, Exception> updateOutboxEventStatus(OutboxEventInventoryUpdateRequest request, UUID id);

    Result<OutboxEventInventory, Exception> getOutboxEventById(UUID id);

    Result<List<OutboxEventInventory>, Exception> getAllOutboxEvents();
}
