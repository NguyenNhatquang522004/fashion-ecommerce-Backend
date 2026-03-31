package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger.StrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RESERVED implements IStrategyStockReservation {
    private final StrategyInventoryLedger strategyInventoryLedger;
    private final IStockReservationRepository stockReservationRepository;
    private final IWarehouseRepository warehouseRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.RESERVED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        try {
            CompletableFuture<Warehouse> warehouseFuture = CompletableFuture.supplyAsync(() -> 
                warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found")), virtualThreadExecutor);

            CompletableFuture<Optional<StockReservation>> stockReservationFuture = CompletableFuture.supplyAsync(() -> 
                stockReservationRepository.findByOrderIdAndSkuCodeAndWarehouseId(
                        request.getOrderId(), request.getSkuCode(), request.getWarehouseId()), virtualThreadExecutor);

            CompletableFuture.allOf(warehouseFuture, stockReservationFuture).join();

            Warehouse warehouse = warehouseFuture.join();
            Optional<StockReservation> stockReservation = stockReservationFuture.join();

            StockReservation stockReservationdata;
            if (stockReservation.isEmpty()) {
                stockReservationdata = StockReservation.builder()
                        .orderId(request.getOrderId())
                        .skuCode(request.getSkuCode())
                        .warehouse(warehouse)
                        .quantity(request.getQuantity())
                        .status(ReservationStatusEnum.RESERVED)
                        .build();
            } else {
                stockReservationdata = stockReservation.get();
                stockReservationdata.setQuantity(stockReservationdata.getQuantity() + request.getQuantity());
            }

            InventorySummaryCreateRequestv2 summaryRequest = InventorySummaryCreateRequestv2.builder()
                    .warehouseId(request.getWarehouseId())
                    .skuCode(request.getSkuCode())
                    .reserved(request.getQuantity())
                    .build();

            CompletableFuture<Void> saveReservationFuture = CompletableFuture.runAsync(() -> 
                stockReservationRepository.save(stockReservationdata), virtualThreadExecutor);

            CompletableFuture<Void> strategyInventoryLedgerFuture = CompletableFuture.runAsync(() -> {
                IStrategyInventoryLedger strategy = strategyInventoryLedger.getStrategy(InventoryTransactionTypeEnum.RESERVE);
                Result<Void, Exception> result = strategy.execute(summaryRequest);
                if (result.hasError()) {
                    throw new RuntimeException("InventoryLedger failed: " + result.error().getMessage());
                }
            }, virtualThreadExecutor);

            CompletableFuture<Void> saveLedgerFuture = CompletableFuture.runAsync(() -> {
                InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                        .warehouse(warehouse)
                        .skuCode(request.getSkuCode())
                        .transactionType(InventoryTransactionTypeEnum.RESERVE)
                        .quantityChange(request.getQuantity())
                        .referenceId(request.getOrderId())
                        .note("Stock reservation")
                        .build();
                inventoryLedgerRepository.save(inventoryLedgerCreateRequest);
            }, virtualThreadExecutor);

            CompletableFuture<Void> saveOutboxFuture = CompletableFuture.runAsync(() -> {
                OutboxEventInventory outboxEventInventory = OutboxEventInventory.builder()
                        .aggregateType(warehouse.getId().toString())
                        .aggregateId(request.getOrderId())
                        .type("StockReservedSuccessEvent")
                        .payload(request.toString())
                        .status(OutboxStatusEnum.PENDING)
                        .build();
                outboxEventInventoryRepository.save(outboxEventInventory);
            }, virtualThreadExecutor);

            CompletableFuture.allOf(saveReservationFuture, strategyInventoryLedgerFuture, saveLedgerFuture, saveOutboxFuture).join();

            return Result.success(stockReservationdata);
        } catch (CompletionException e) {
            return Result.error(new Exception(e.getCause() != null ? e.getCause().getMessage() : "Error executing RESERVED strategy", e));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
