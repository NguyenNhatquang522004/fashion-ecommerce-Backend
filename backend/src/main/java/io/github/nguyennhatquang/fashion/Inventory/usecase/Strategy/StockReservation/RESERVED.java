package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.InventoryLedgerRepository;
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

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.RESERVED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        try {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            Optional<StockReservation> stockReservation = stockReservationRepository
                    .findByOrderIdAndSkuCodeAndWarehouseId(request.getOrderId(), request.getSkuCode(),
                            request.getWarehouseId());

            if (stockReservation.isEmpty()) {
                StockReservation stockReservationdata = StockReservation.builder()
                        .orderId(request.getOrderId())
                        .skuCode(request.getSkuCode())
                        .warehouse(warehouse)
                        .quantity(request.getQuantity())
                        .status(ReservationStatusEnum.RESERVED)
                        .build();
                stockReservationRepository.save(stockReservationdata);
            }
            StockReservation StockReservationdata = stockReservation.get();
            StockReservationdata.setQuantity(StockReservationdata.getQuantity() + request.getQuantity());
            stockReservationRepository.save(StockReservationdata);
            InventorySummaryCreateRequestv2 summaryRequest = InventorySummaryCreateRequestv2.builder()
                    .warehouseId(request.getWarehouseId())
                    .skuCode(request.getSkuCode())
                    .reserved(request.getQuantity())
                    .build();
            IStrategyInventoryLedger strategy = strategyInventoryLedger
                    .getStrategy(InventoryTransactionTypeEnum.RESERVE);
            Result<Void, Exception> result = strategy.execute(summaryRequest);
            if (result.hasError()) {
                return Result.error(new Exception("InventoryLedger failed"));
            }
            InventoryLedger inventoryLedgerCreateRequest = InventoryLedger.builder()
                    .warehouse(warehouse)
                    .skuCode(request.getSkuCode())
                    .transactionType(InventoryTransactionTypeEnum.RESERVE)
                    .quantityChange(request.getQuantity())
                    .referenceId(request.getOrderId())
                    .note("Stock reservation")
                    .build();
            inventoryLedgerRepository.save(inventoryLedgerCreateRequest);

            OutboxEventInventory outboxEventInventory = outboxEventInventoryRepository
                    .findByAggregateId(request.getOrderId());
            if (outboxEventInventory != null) {
                outboxEventInventory.setPayload(request.toString());
                outboxEventInventory.setType("StockReservedSuccessEvent");
                outboxEventInventoryRepository.save(outboxEventInventory);
                return Result.success(null);
            }
            outboxEventInventory = OutboxEventInventory.builder()
                    .aggregateType("Inventory")
                    .aggregateId(request.getOrderId())
                    .type("StockReservedSuccessEvent")
                    .payload(request.toString())
                    .status(OutboxStatusEnum.PENDING)
                    .build();
            outboxEventInventoryRepository.save(outboxEventInventory);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
