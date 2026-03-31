package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.ICronJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EXPIRED implements IStrategyStockReservation {
    private final ICronJobScheduler cronJobScheduler;
    private final IStockReservationRepository stockReservationRepository;
    private final IInventorySummaryRepository inventorySummaryRepository;
    private final IInventoryLedgerRepository inventoryLedgerRepository;
    private final IOutboxEventInventoryRepository outboxEventInventoryRepository;
    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.EXPIRED;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreatePayload request) {
        int batchSize = 1000;
        Pageable limit = PageRequest.of(0, batchSize);
        OffsetDateTime now = OffsetDateTime.now();
        try {
            cronJobScheduler.scheduleJob(
                    "sync-fashion-inventory-job",
                    () -> {
                        try {
                            log.info("Starting sync-fashion-inventory-job");
                            List<StockReservation> expiredList = stockReservationRepository
                                    .findExpiredReservationsWithLimit(
                                            ReservationStatusEnum.RESERVED, now, limit);
                            if (expiredList.isEmpty()) {
                                log.info("Không có StockReservation nào hết hạn cần xử lý.");
                                return;
                            }
                            List<CompletableFuture<StockReservation>> futures = expiredList.stream()
                                    .map(reservation -> CompletableFuture
                                            .supplyAsync(() -> processSingleReservation(reservation),
                                                    virtualThreadExecutor)
                                            // Bắt lỗi từng task, trả về null nếu lỗi để các task khác vẫn chạy 100%
                                            .exceptionally(ex -> {
                                                log.error("Lỗi khi xử lý reservation ID: {}", reservation.getId(), ex);
                                                return null;
                                            }))
                                    .toList();
                            List<StockReservation> processedReservations = CompletableFuture
                                    .allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenApply(v -> futures.stream()
                                            .map(CompletableFuture::join)
                                            .filter(Objects::nonNull) // Loại bỏ các task bị lỗi (null)
                                            .toList())
                                    .join();
                            if (!processedReservations.isEmpty()) {
                                stockReservationRepository.saveAll(processedReservations);
                                log.info("Đã cập nhật {} bản ghi.", processedReservations.size());
                            }
                        } catch (Exception e) {
                            log.error("Error in sync-fashion-inventory-job", e);
                        }
                    },
                    "0 */5 * * * ?");
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    private StockReservation processSingleReservation(StockReservation reservation) {
        Optional<InventorySummary> summary = inventorySummaryRepository
                .findByWarehouseIdAndSkuCode(reservation.getWarehouse().getId(), reservation.getSkuCode());
        if (summary.isEmpty()) {
            return null;
        }
        InventorySummary inventorySummary = summary.get();
        inventorySummary.setReserved(inventorySummary.getReserved() - reservation.getQuantity());
        inventorySummary.setAvailable(inventorySummary.getAvailable() + reservation.getQuantity());
        inventorySummary.setUpdatedAt(OffsetDateTime.now());
        inventorySummary.setUpdatedBy(reservation.getCreatedBy());
        inventorySummary.setVersion(inventorySummary.getVersion() + 1);
        inventorySummaryRepository.save(inventorySummary);
        reservation.setStatus(ReservationStatusEnum.EXPIRED);
        reservation.setUpdatedAt(OffsetDateTime.now());
        reservation.setUpdatedBy(reservation.getCreatedBy());
        reservation.setVersion(reservation.getVersion() + 1);
        InventoryLedger InventoryLedgerdata = InventoryLedger.builder()
                .warehouse(reservation.getWarehouse())
                .skuCode(reservation.getSkuCode())
                .transactionType(InventoryTransactionTypeEnum.RELEASE)
                .quantityChange(-reservation.getQuantity())
                .referenceId(reservation.getOrderId())
                .note("Stock reservation expired")
                .build();
        inventoryLedgerRepository.save(InventoryLedgerdata);
        OutboxEventInventory outboxEventInventory = OutboxEventInventory.builder()
                .aggregateId(reservation.getOrderId())
                .aggregateType(reservation.getWarehouse().getId().toString())
                .type(InventoryTransactionTypeEnum.RELEASE.toString())
                .payload(reservation.toString())
                .status(OutboxStatusEnum.FAILED)
                .build();
        outboxEventInventoryRepository.save(outboxEventInventory);
        return reservation;
    }

}
