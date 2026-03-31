package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CANCELLED implements IStrategyStockReservation {
    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IWarehouseRepository warehouseRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.CANCELLED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        try {
            Optional<Warehouse> warehouse = warehouseRepository.findById(request.getWarehouseId());
            if (warehouse.isEmpty()) {
                return Result.error(new Exception("Warehouse not found"));
            }
            Optional<StockReservation> stockReservation = stockReservationRepository
                    .findByOrderIdAndSkuCodeAndWarehouseId(request.getOrderId(), request.getSkuCode(),
                            request.getWarehouseId());
            if (stockReservation.isEmpty()) {
                return Result.error(new Exception("Stock reservation not found"));
            }
            StockReservation stockReservationdata = stockReservation.get();

            stockReservationdata.setStatus(ReservationStatusEnum.CANCELLED);
            stockReservationdata.setIsDeleted(true);
            stockReservationRepository.save(stockReservationdata);
            Optional<InventorySummary> inventorySummary = inventorySummaryRepository
                    .findByWarehouseIdAndSkuCode(request.getWarehouseId(), request.getSkuCode());
            if (inventorySummary.isEmpty()) {
                return Result.error(new Exception("Inventory summary not found"));
            }
            InventorySummary inventorySummarydata = inventorySummary.get();
            inventorySummarydata.setReserved(inventorySummarydata.getReserved() + stockReservationdata.getQuantity());
            inventorySummarydata.setAvailable(inventorySummarydata.getOnHand() - inventorySummarydata.getReserved());
            inventorySummaryRepository.save(inventorySummarydata);
            InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                    .warehouse(warehouse.get())
                    .skuCode(request.getSkuCode())
                    .transactionType(InventoryTransactionTypeEnum.RELEASE)
                    .quantityChange(stockReservationdata.getQuantity())
                    .referenceId(request.getOrderId())
                    .note("Stock cancelled")
                    .build();
            inventoryLedgerRepository.save(inventoryLedgerCreateRequest);
            OutboxEventInventory outboxEventInventory = outboxEventInventoryRepository
                    .findByAggregateId(request.getOrderId());
            if (outboxEventInventory == null) {
                return Result.error(new Exception("Outbox event inventory not found"));
            }
            outboxEventInventory.setPayload(request.toString());
            outboxEventInventory.setType("StockCancelledSuccessEvent");
            outboxEventInventory.setStatus(OutboxStatusEnum.PUBLISHED);
            outboxEventInventoryRepository.save(outboxEventInventory);
            return Result.success(stockReservationdata);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
