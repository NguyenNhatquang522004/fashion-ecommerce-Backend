package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminInventoryLedgerUseCase {
    Result<InventoryLedger, Exception> createInventoryLedger(InventoryLedgerCreateRequest request);

    Result<InventoryLedger, Exception> getInventoryLedgerById(UUID id);

    Result<List<InventoryLedger>, Exception> getAllInventoryLedgers();
}
