package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.ICronJobScheduler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EXPIRED implements IStrategyStockReservation {
    private final ICronJobScheduler cronJobScheduler;
    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.EXPIRED;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        try {
            cronJobScheduler.scheduleJob(
                    "sync-fashion-inventory-job",
                    () -> {
                        
                    }, 
                    "0 0 2 * * ?");
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
