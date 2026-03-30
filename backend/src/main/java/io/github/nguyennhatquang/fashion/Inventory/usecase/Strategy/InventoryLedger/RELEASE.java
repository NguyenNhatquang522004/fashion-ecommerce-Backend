package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RELEASE implements IStrategyInventoryLedger {
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IWarehouseRepository warehouseRepository;

    @Override
    public InventoryTransactionTypeEnum getType() {
        return InventoryTransactionTypeEnum.RELEASE;
    }

    @Override
    public Result<Void, Exception> execute(InventorySummaryCreateRequestv2 request) {
        try {
            Optional<InventorySummary> inventorySummary = inventorySummaryRepository
                    .findByWarehouseIdAndSkuCodeForUpdate(request.warehouseId(), request.skuCode());
            if (inventorySummary.isEmpty()) {
                return Result.error(new Exception("Inventory summary not found"));
            }
            InventorySummary inventorySummaryData = inventorySummary.get();
            if (inventorySummaryData.getReserved() < request.reserved()) {
                return Result.error(new Exception("Inventory summary not found"));
            }
            inventorySummaryData.setReserved(inventorySummaryData.getReserved() - request.reserved());
            inventorySummaryData.setAvailable(inventorySummaryData.getOnHand() - inventorySummaryData.getReserved());
            inventorySummaryRepository.save(inventorySummaryData);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
