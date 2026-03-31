package io.github.nguyennhatquang.fashion.Inventory.infrastructure.Consumer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.common.shared.ICronJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerClearStockReservation {
    private final ICronJobScheduler cronJobScheduler;
    private final IStockReservationRepository stockReservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void registerSchedules() {
        // Đồng bộ hàng hóa lúc 2:00 AM mỗi ngày
        cronJobScheduler.scheduleJob(
                "clear-expired-stock-reservation-job",
                this::clearExpiredStockReservation,
                "0 0 2 * * ?");
    }

    private void clearExpiredStockReservation() {
        log.info("Starting to clear expired stock reservations");
        try {
            // stockReservationRepository.clearExpiredStockReservations();
            log.info("Successfully cleared expired stock reservations");
        } catch (Exception e) {
            log.error("Error clearing expired stock reservations", e);
        }
    }
}
