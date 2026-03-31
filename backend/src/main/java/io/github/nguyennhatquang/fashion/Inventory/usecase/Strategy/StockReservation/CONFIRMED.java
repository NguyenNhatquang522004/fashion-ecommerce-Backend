package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;

import org.neo4j.cypherdsl.core.Return;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
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
public class CONFIRMED implements IStrategyStockReservation {

    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IWarehouseRepository warehouseRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.CONFIRMED;
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
            stockReservationdata.setStatus(ReservationStatusEnum.CONFIRMED);
            stockReservationRepository.save(stockReservationdata);
            Optional<InventorySummary> inventorySummary = inventorySummaryRepository
                    .findByWarehouseIdAndSkuCode(request.getWarehouseId(), request.getSkuCode());
            if (inventorySummary.isEmpty()) {
                return Result.error(new Exception("Inventory summary not found"));
            }
            if (inventorySummary.get().getAvailable() < stockReservationdata.getQuantity()) {
                return Result.error(new Exception("Inventory summary not enough"));
            }
            InventorySummary inventorySummarydata = inventorySummary.get();
            inventorySummarydata.setOnHand(inventorySummarydata.getOnHand() - stockReservationdata.getQuantity());
            inventorySummarydata.setReserved(inventorySummarydata.getReserved() - stockReservationdata.getQuantity());
            inventorySummarydata.setAvailable(inventorySummarydata.getOnHand() - inventorySummarydata.getReserved());
            inventorySummaryRepository.save(inventorySummarydata);
            InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                    .warehouse(warehouse.get())
                    .skuCode(request.getSkuCode())
                    .transactionType(InventoryTransactionTypeEnum.STOCK_OUT)
                    .quantityChange(stockReservationdata.getQuantity())
                    .referenceId(request.getOrderId())
                    .note("Stock confirmed")
                    .build();
            inventoryLedgerRepository.save(inventoryLedgerCreateRequest);
            OutboxEventInventory outboxEventInventory = outboxEventInventoryRepository
                    .findByAggregateId(request.getOrderId());
            if (outboxEventInventory != null) {
                return Result.error(new Exception("Outbox event inventory already exists"));
            }
            outboxEventInventory = OutboxEventInventory.builder()
                    .aggregateType("Inventory")
                    .aggregateId(request.getOrderId())
                    .type("StockConfirmedSuccessEvent")
                    .payload(request.toString())
                    .status(OutboxStatusEnum.PUBLISHED)
                    .build();
            outboxEventInventoryRepository.save(outboxEventInventory);
            return Result.success(stockReservationdata);
        } catch (Exception e) {
            return Result.error(e);
        }

    }

}
