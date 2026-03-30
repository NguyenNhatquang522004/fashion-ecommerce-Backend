package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CANCELLED implements IStrategyStockReservation {
    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.CANCELLED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreateRequestv2 request) {
        try {
            Optional<StockReservation> stockReservation = stockReservationRepository
                    .findByOrderIdAndSkuCodeAndWarehouseId(request.orderId(), request.skuCode(), request.warehouseId());
            if (stockReservation.isEmpty()) {
                return Result.error(new Exception("Stock reservation not found"));
            }
            StockReservation stockReservationdata = stockReservation.get();

            stockReservationdata.setStatus(ReservationStatusEnum.CANCELLED);
            stockReservationRepository.save(stockReservationdata);
            Optional<InventorySummary> inventorySummary = inventorySummaryRepository
                    .findByWarehouseIdAndSkuCode(request.warehouseId(), request.skuCode());
            if (inventorySummary.isEmpty()) {
                return Result.error(new Exception("Inventory summary not found"));
            }
            InventorySummary inventorySummarydata = inventorySummary.get();
            inventorySummarydata.setReserved(inventorySummarydata.getReserved() - stockReservationdata.getQuantity());
            inventorySummarydata.setAvailable(inventorySummarydata.getOnHand() - inventorySummarydata.getReserved());
            inventorySummaryRepository.save(inventorySummarydata);
            return Result.success(stockReservationdata);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
