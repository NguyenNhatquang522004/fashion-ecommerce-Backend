package io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    @Query("SELECT u FROM StockReservation u WHERE u.orderId = :orderId AND u.skuCode = :skuCode AND u.warehouseId = :warehouseId")
    Optional<StockReservation> findByOrderIdAndSkuCodeAndWarehouseId(String orderId, String skuCode, UUID warehouseId);

    @Query("SELECT COUNT(u.id) FROM StockReservation u WHERE u.createdAt <= :snapshotTime")
    long countBySnapshot(@Param("snapshotTime") Instant snapshotTime);

    // 2. Chỉ lấy ID (Deferred Join - Cực kỳ nhanh vì chỉ quét qua Index)
    @Query("SELECT u.id FROM StockReservation u WHERE u.createdAt <= :snapshotTime ORDER BY u.createdAt DESC, u.id DESC")
    List<UUID> findIdsBySnapshot(
            @Param("snapshotTime") Instant snapshotTime,
            Pageable pageable);

    // 3. Lấy Full Data từ tập ID đã lọc (Sắp xếp lại trên DB để đảm bảo thứ tự)
    @Query("SELECT u FROM StockReservation u WHERE u.id IN :ids ORDER BY u.createdAt DESC, u.id DESC")
    List<StockReservation> fetchFullDataByIds(@Param("ids") List<UUID> ids);

    @Modifying
    @Query("UPDATE StockReservation s " +
            "SET s.isDeleted = true, s.updatedAt = :now " +
            "WHERE s.expiresAt <= :now AND s.isDeleted = false")
    int expireExpiredReservations(@Param("now") OffsetDateTime now);
}
