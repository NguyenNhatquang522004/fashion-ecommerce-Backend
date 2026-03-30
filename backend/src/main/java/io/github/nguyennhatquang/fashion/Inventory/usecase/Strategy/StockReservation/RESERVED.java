package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger.StrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RESERVED implements IStrategyStockReservation {
    private final StrategyInventoryLedger strategyInventoryLedger;
    private final IStockReservationRepository stockReservationRepository;
    private final IWarehouseRepository warehouseRepository;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.RESERVED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreateRequestv2 request) {
        try {
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            Optional<StockReservation> stockReservation = stockReservationRepository
                    .findByOrderIdAndSkuCodeAndWarehouseId(request.orderId(), request.skuCode(), request.warehouseId());

            if (stockReservation.isEmpty()) {
                StockReservation stockReservationdata = StockReservation.builder()
                        .orderId(request.orderId())
                        .skuCode(request.skuCode())
                        .warehouse(warehouse)
                        .quantity(request.quantity())
                        .status(ReservationStatusEnum.RESERVED)
                        .build();
                stockReservationRepository.save(stockReservationdata);
            }
            StockReservation StockReservationdata = stockReservation.get();
            StockReservationdata.setQuantity(StockReservationdata.getQuantity() + request.quantity());
            stockReservationRepository.save(StockReservationdata);
            InventorySummaryCreateRequestv2 summaryRequest = InventorySummaryCreateRequestv2.builder()
                    .warehouseId(request.warehouseId())
                    .skuCode(request.skuCode())
                    .reserved(request.quantity())
                    .build();
            IStrategyInventoryLedger strategy = strategyInventoryLedger
                    .getStrategy(InventoryTransactionTypeEnum.RESERVE);
            Result<Void, Exception> result = strategy.execute(summaryRequest);
            if (result.hasError()) {
                return Result.error(new Exception("InventoryLedger failed"));
            }
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
