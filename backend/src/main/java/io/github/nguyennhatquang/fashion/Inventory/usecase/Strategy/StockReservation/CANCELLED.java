package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
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

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.CANCELLED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        try {
            CompletableFuture<Warehouse> warehouseFuture = CompletableFuture.supplyAsync(() -> 
                warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found")), virtualThreadExecutor);

            CompletableFuture<StockReservation> stockReservationFuture = CompletableFuture.supplyAsync(() -> 
                stockReservationRepository.findByOrderIdAndSkuCodeAndWarehouseId(
                        request.getOrderId(), request.getSkuCode(), request.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Stock reservation not found")), virtualThreadExecutor);

            CompletableFuture<InventorySummary> inventorySummaryFuture = CompletableFuture.supplyAsync(() -> 
                inventorySummaryRepository.findByWarehouseIdAndSkuCode(
                        request.getWarehouseId(), request.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Inventory summary not found")), virtualThreadExecutor);

            // Wait for dependencies in parallel
            CompletableFuture.allOf(warehouseFuture, stockReservationFuture, inventorySummaryFuture).join();

            Warehouse warehouse = warehouseFuture.join();
            StockReservation stockReservationdata = stockReservationFuture.join();
            InventorySummary inventorySummarydata = inventorySummaryFuture.join();

            // Process mutations
            stockReservationdata.setStatus(ReservationStatusEnum.CANCELLED);
            stockReservationdata.setIsDeleted(true);

            inventorySummarydata.setReserved(Math.max(0, inventorySummarydata.getReserved() - stockReservationdata.getQuantity()));
            inventorySummarydata.setAvailable(inventorySummarydata.getOnHand() - inventorySummarydata.getReserved());

            // Save asynchronously in parallel
            CompletableFuture<Void> saveReservationFuture = CompletableFuture.runAsync(() -> 
                stockReservationRepository.save(stockReservationdata), virtualThreadExecutor);

            CompletableFuture<Void> saveSummaryFuture = CompletableFuture.runAsync(() -> 
                inventorySummaryRepository.save(inventorySummarydata), virtualThreadExecutor);

            CompletableFuture<Void> saveLedgerFuture = CompletableFuture.runAsync(() -> {
                InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                        .warehouse(warehouse)
                        .skuCode(request.getSkuCode())
                        .transactionType(InventoryTransactionTypeEnum.RELEASE)
                        .quantityChange(-stockReservationdata.getQuantity())
                        .referenceId(request.getOrderId())
                        .note("Stock cancelled")
                        .build();
                inventoryLedgerRepository.save(inventoryLedgerCreateRequest);
            }, virtualThreadExecutor);

            CompletableFuture<Void> saveOutboxFuture = CompletableFuture.runAsync(() -> {
                OutboxEventInventory outboxEventInventory = OutboxEventInventory.builder()
                        .aggregateType(warehouse.getId().toString())
                        .aggregateId(request.getOrderId())
                        .type("StockCancelledSuccessEvent")
                        .payload(request.toString())
                        .status(OutboxStatusEnum.PENDING)
                        .build();
                outboxEventInventoryRepository.save(outboxEventInventory);
            }, virtualThreadExecutor);

            CompletableFuture.allOf(saveReservationFuture, saveSummaryFuture, saveLedgerFuture, saveOutboxFuture).join();

            return Result.success(stockReservationdata);
        } catch (CompletionException e) {
            return Result.error(new Exception(e.getCause() != null ? e.getCause().getMessage() : "Error executing CANCELLED strategy", e));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
