package io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IStrategyInventoryLedger {
    InventoryTransactionTypeEnum getType();

    Result<Void, Exception> execute(InventorySummaryCreateRequestv2 request);
}
