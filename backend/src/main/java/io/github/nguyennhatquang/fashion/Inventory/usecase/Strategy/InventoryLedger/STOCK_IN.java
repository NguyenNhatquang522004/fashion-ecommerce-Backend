package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventorySummaryMapper;
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
public class STOCK_IN implements IStrategyInventoryLedger {
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IWarehouseRepository warehouseRepository;
    private final InventorySummaryMapper summaryMapper;

    @Override
    public InventoryTransactionTypeEnum getType() {
        return InventoryTransactionTypeEnum.STOCK_IN;
    }

    @Override
    public Result<Void, Exception> execute(InventorySummaryCreateRequestv2 request) {
        try {
            Optional<InventorySummary> inventorySummary = inventorySummaryRepository
                    .findByWarehouseIdAndSkuCodeForUpdate(request.warehouseId(), request.skuCode());
            if (inventorySummary.isEmpty()) {
                Optional<Warehouse> warehouse = warehouseRepository.findById(request.warehouseId());
                if (warehouse.isEmpty()) {
                    return Result.error(new Exception("Warehouse not found"));
                }
                InventorySummary inventorySummaryData = summaryMapper.toEntity(request);
                inventorySummaryData.setAvailable(request.onHand());
                inventorySummaryData.setReserved(0);
                inventorySummaryData.setWarehouse(warehouse.get());
                inventorySummaryRepository.save(inventorySummaryData);
                return Result.success(null);
            }
            InventorySummary inventorySummaryData = inventorySummary.get();
            inventorySummaryData.setOnHand(inventorySummaryData.getOnHand() + request.onHand());
            inventorySummaryData.setAvailable(inventorySummaryData.getOnHand() - inventorySummaryData.getReserved());
            inventorySummaryRepository.save(inventorySummaryData);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
