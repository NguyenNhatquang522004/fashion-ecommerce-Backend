package io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventorySummaryRepository extends JpaRepository<InventorySummary, UUID> {

    @Query("SELECT i FROM InventorySummary i " +
            "JOIN FETCH i.warehouse " +
            "WHERE i.warehouse.id = :warehouseId " +
            "AND i.skuCode = :skuCode")
    Optional<InventorySummary> findByWarehouseIdAndSkuCode(
            @Param("warehouseId") UUID warehouseId,
            @Param("skuCode") String skuCode);

    /**
     * PHIÊN BẢN DÙNG CHO CẬP NHẬT (FLASH SALE / TRỪ KHO)
     * * Nếu bạn tìm để sau đó UPDATE (trừ kho), hãy dùng PESSIMISTIC_WRITE
     * để khóa dòng dữ liệu này lại, ngăn chặn race condition tuyệt đối.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventorySummary i " +
            "WHERE i.warehouse.id = :warehouseId " +
            "AND i.skuCode = :skuCode")
    Optional<InventorySummary> findByWarehouseIdAndSkuCodeForUpdate(
            @Param("warehouseId") UUID warehouseId,
            @Param("skuCode") String skuCode);

    @Query("SELECT COUNT(u.id) FROM InventorySummary u WHERE u.createdAt <= :snapshotTime")
    long countBySnapshot(@Param("snapshotTime") Instant snapshotTime);

    // 2. Chỉ lấy ID (Deferred Join - Cực kỳ nhanh vì chỉ quét qua Index)
    @Query("SELECT u.id FROM InventorySummary u WHERE u.createdAt <= :snapshotTime ORDER BY u.createdAt DESC, u.id DESC")
    List<UUID> findIdsBySnapshot(
            @Param("snapshotTime") Instant snapshotTime,
            Pageable pageable);

    // 3. Lấy Full Data từ tập ID đã lọc (Sắp xếp lại trên DB để đảm bảo thứ tự)
    @Query("SELECT u FROM InventorySummary u WHERE u.id IN :ids ORDER BY u.createdAt DESC, u.id DESC")
    List<InventorySummary> fetchFullDataByIds(@Param("ids") List<UUID> ids);
}
