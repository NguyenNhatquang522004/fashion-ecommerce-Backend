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
public class CONFIRMED implements IStrategyStockReservation {

    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IWarehouseRepository warehouseRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.CONFIRMED;
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

            CompletableFuture.allOf(warehouseFuture, stockReservationFuture, inventorySummaryFuture).join();

            Warehouse warehouse = warehouseFuture.join();
            StockReservation stockReservationdata = stockReservationFuture.join();
            InventorySummary inventorySummarydata = inventorySummaryFuture.join();

            if (inventorySummarydata.getAvailable() < stockReservationdata.getQuantity()) {
                throw new RuntimeException("Inventory summary not enough");
            }

            stockReservationdata.setStatus(ReservationStatusEnum.CONFIRMED);

            inventorySummarydata.setOnHand(inventorySummarydata.getOnHand() - stockReservationdata.getQuantity());
            inventorySummarydata.setReserved(inventorySummarydata.getReserved() - stockReservationdata.getQuantity());
            inventorySummarydata.setAvailable(inventorySummarydata.getOnHand() - inventorySummarydata.getReserved());

            CompletableFuture<Void> saveReservationFuture = CompletableFuture.runAsync(() -> 
                stockReservationRepository.save(stockReservationdata), virtualThreadExecutor);

            CompletableFuture<Void> saveSummaryFuture = CompletableFuture.runAsync(() -> 
                inventorySummaryRepository.save(inventorySummarydata), virtualThreadExecutor);

            CompletableFuture<Void> saveLedgerFuture = CompletableFuture.runAsync(() -> {
                InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                        .warehouse(warehouse)
                        .skuCode(request.getSkuCode())
                        .transactionType(InventoryTransactionTypeEnum.STOCK_OUT)
                        .quantityChange(stockReservationdata.getQuantity())
                        .referenceId(request.getOrderId())
                        .note("Stock confirmed")
                        .build();
                inventoryLedgerRepository.save(inventoryLedgerCreateRequest);
            }, virtualThreadExecutor);

            CompletableFuture<Void> saveOutboxFuture = CompletableFuture.runAsync(() -> {
                OutboxEventInventory outboxEventInventory = OutboxEventInventory.builder()
                        .aggregateType("Inventory")
                        .aggregateId(request.getOrderId())
                        .type("StockConfirmedSuccessEvent")
                        .payload(request.toString())
                        .status(OutboxStatusEnum.PUBLISHED)
                        .build();
                outboxEventInventoryRepository.save(outboxEventInventory);
            }, virtualThreadExecutor);

            CompletableFuture.allOf(saveReservationFuture, saveSummaryFuture, saveLedgerFuture, saveOutboxFuture).join();

            return Result.success(stockReservationdata);
        } catch (CompletionException e) {
            return Result.error(new Exception(e.getCause() != null ? e.getCause().getMessage() : "Error executing CONFIRMED strategy", e));
        } catch (Exception e) {
            return Result.error(e);
        }

    }

}
