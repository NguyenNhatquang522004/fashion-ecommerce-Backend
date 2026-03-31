package io.github.nguyennhatquang.fashion.Order.infrastructure.Repository;

import io.github.nguyennhatquang.fashion.Order.domain.entity.Shipment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    @Query("SELECT COUNT(u.id) FROM Shipment u WHERE u.createdAt <= :snapshotTime")
    long countBySnapshot(@Param("snapshotTime") Instant snapshotTime);

    @Query("SELECT u.id FROM Shipment u WHERE u.createdAt <= :snapshotTime ORDER BY u.createdAt DESC, u.id DESC")
    List<UUID> findIdsBySnapshot(
            @Param("snapshotTime") Instant snapshotTime,
            Pageable pageable);

    @Query("SELECT u FROM Shipment u WHERE u.id IN :ids ORDER BY u.createdAt DESC, u.id DESC")
    List<Shipment> fetchFullDataByIds(@Param("ids") List<UUID> ids);
}
